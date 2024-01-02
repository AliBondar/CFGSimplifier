package org.example;

import java.util.*;

public class CFGSimplifier {
    public static void main(String[] args) {
        // Original context-free grammar
        Map<Character, List<String>> grammar = new HashMap<>();
        grammar.put('S', Arrays.asList("ABCd", "ABd", "ACd", "BCd", "Ad", "Bd", "d"));
        grammar.put('A', Arrays.asList("BC", "B", "C"));
        grammar.put('B', Arrays.asList("bB", "b"));
        grammar.put('C', Arrays.asList("cC", "c"));
        grammar.put('D', Arrays.asList("d", "c"));

        // Remove null productions
        removeNullProductions(grammar);

        // Remove unit productions
        removeUnitProductions(grammar);

        // Remove useless productions
        removeUselessProductions(grammar);

        // Print the simplified grammar
        System.out.println("Simplified Grammar:");
        for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            for (String production : entry.getValue()) {
                System.out.print(production + " | ");
            }
            System.out.println();
        }
    }

    private static void removeNullProductions(Map<Character, List<String>> grammar) {
        Set<Character> nullable = new HashSet<>();
        boolean changed;

        do {
            changed = false;
            for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
                char variable = entry.getKey();
                List<String> productions = entry.getValue();

                for (String production : productions) {
                    if (production.isEmpty()) {
                        if (!nullable.contains(variable)) {
                            nullable.add(variable);
                            changed = true;
                        }
                    } else {
                        boolean isNullable = true;
                        for (char symbol : production.toCharArray()) {
                            if (!nullable.contains(symbol)) {
                                isNullable = false;
                                break;
                            }
                        }
                        if (isNullable && !nullable.contains(variable)) {
                            nullable.add(variable);
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);

        // Remove null productions from the grammar
        for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
            List<String> productions = entry.getValue();
            productions.removeIf(String::isEmpty);
            for (int i = 0; i < productions.size(); i++) {
                String originalProduction = productions.get(i);
                for (int j = 0; j < originalProduction.length(); j++) {
                    if (nullable.contains(originalProduction.charAt(j))) {
                        String newProduction = originalProduction.replace(String.valueOf(originalProduction.charAt(j)), "");
                        if (!productions.contains(newProduction) && !newProduction.isEmpty()) {
                            productions.add(newProduction);
                        }
                    }
                }
            }
        }
    }

    private static void removeUnitProductions(Map<Character, List<String>> grammar) {
        Set<Character> unitProductions = new HashSet<>();
        for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
            char variable = entry.getKey();
            List<String> productions = entry.getValue();
            List<String> newProductions = new ArrayList<>();
            for (String production : productions) {
                if (production.length() == 1 && Character.isUpperCase(production.charAt(0))) {
                    unitProductions.add(production.charAt(0));
                } else {
                    newProductions.add(production);
                }
            }
            grammar.put(variable, newProductions);
        }

        for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
            char variable = entry.getKey();
            List<String> productions = entry.getValue();
            List<String> updatedProductions = new ArrayList<>(productions);
            for (String production : productions) {
                for (char unit : unitProductions) {
                    if (production.contains(String.valueOf(unit))) {
                        updatedProductions.remove(production);
                        updatedProductions.addAll(grammar.get(unit));
                        grammar.put(variable, updatedProductions);
                        break;
                    }
                }
            }
        }
    }

    private static void removeUselessProductions(Map<Character, List<String>> grammar) {
        // Implementation to remove useless productions
        Set<Character> reachable = new HashSet<>();
        Set<Character> useful = new HashSet<>();
        useful.add('S');

        boolean changed;
        do {
            changed = false;
            for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
                char variable = entry.getKey();
                List<String> productions = entry.getValue();
                if (useful.contains(variable)) {
                    for (String production : productions) {
                        for (char symbol : production.toCharArray()) {
                            if (Character.isUpperCase(symbol)) {
                                if (!reachable.contains(symbol)) {
                                    reachable.add(symbol);
                                    changed = true;
                                }
                                if (!useful.contains(symbol)) {
                                    useful.add(symbol);
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        } while (changed);

        // Remove unreachable and non-useful productions
        grammar.keySet().retainAll(useful);
    }
}

