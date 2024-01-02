package org.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        Map<Character, List<String>> grammar = new HashMap<>();

        // Populate the grammar
        grammar.put('S', Arrays.asList("aA", "bBc", "C"));
        grammar.put('A', Arrays.asList("aAa"));
        grammar.put('B', Arrays.asList("ab"));
        grammar.put('C', Arrays.asList("ac"));

        System.out.println("Here is the grammar :");
        for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            for (String production : entry.getValue()) {
                System.out.print(production + " | ");
            }
            System.out.println();
        }

        // Remove unreachable variables
        Set<Character> reachable = new HashSet<>();
        findReachable(grammar, reachable, 'S');

        grammar.keySet().retainAll(reachable);

        System.out.println("Here is the grammar after removing the unreachable variables :");
        for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            for (String production : entry.getValue()) {
                System.out.print(production + " | ");
            }
            System.out.println();
        }

        // Remove non-generating variables
        Set<Character> generating = new HashSet<>();
        findGenerating(grammar, generating);

        grammar.keySet().retainAll(generating);



        // Print the simplified grammar
        System.out.println("Here is the simplified grammar :");
        for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            for (String production : entry.getValue()) {
                System.out.print(production + " | ");
            }
            System.out.println();
        }
    }

    private static void findReachable(Map<Character, List<String>> grammar, Set<Character> reachable, char symbol) {
        if (reachable.contains(symbol)) {
            return;
        }
        reachable.add(symbol);
        for (String production : grammar.getOrDefault(symbol, Collections.emptyList())) {
            for (char c : production.toCharArray()) {
                if (grammar.containsKey(c)) {
                    findReachable(grammar, reachable, c);
                }
            }
        }
    }

    private static void findGenerating(Map<Character, List<String>> grammar, Set<Character> generating) {
        boolean changed;
        do {
            changed = false;
            for (Map.Entry<Character, List<String>> entry : grammar.entrySet()) {
                char symbol = entry.getKey();
                if (generating.contains(symbol)) {
                    continue;
                }
                for (String production : entry.getValue()) {
                    boolean canGenerate = true;
                    for (char c : production.toCharArray()) {
                        if (!generating.contains(c)) {
                            canGenerate = false;
                            break;
                        }
                    }
                    if (canGenerate) {
                        generating.add(symbol);
                        changed = true;
                        break;
                    }
                }
            }
        } while (changed);
    }
}