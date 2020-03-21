package Lesson3;

import java.util.*;

public class CollectionOne {
    public static void main(String[] args) {
        vocabulary();
    }

    private static void vocabulary() {
        String[] Arr = new String[]{"Россия", "Москва", "ученик", "ученица", "ребята", "соловей", "ученик", "соловей", "малина", "карандаш", "ученик", "Москва", "ворона"};
        Set<String> set = new HashSet<String>(Arrays.asList(Arr));
        System.out.println(set);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < Arr.length; i++) list.add(Arr[i]);

        Collections.sort(list);

        String  number = list.get(0);
        int count = 0;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i) != number){
                number = list.get(i);
                System.out.println("[" + list.get(i-1) + "] - " + count + ",");
                count = 1;
            }
            else count++;
        }
        System.out.println("[" + list.get(list.size()-1) + "] - " + count + ";");
        
    }
}
