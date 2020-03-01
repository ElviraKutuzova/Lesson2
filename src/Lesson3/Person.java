package Lesson3;

import java.util.ArrayList;
import java.util.List;


public class Person implements Comparable{
private String name;
private String telephone;
public Person(String name, String telephone){
    this.name = name;
    this.telephone = telephone;
    addPerson(this);
}
private  static List<Person> list = new ArrayList<Person>();

public String getName() {return name;}
public String getTelephone() {return telephone;}
public void setName(String name) {this.name = name;}
public void setTelephone(String telephone) {this.telephone = telephone;}

public static void addPerson(Person person){
  list.add(person);
}
public static void removePerson(Person person){
    list.remove(person);
}
public static void findPerson(String Name){
    for(Person person : list){
        if(person.name.equals(Name)){
            System.out.println(person.name + " - " + person.telephone);
        }

    }
}

public static void printAll(){
    System.out.println("Телефонный справочник: ");
    for (Person person : list){
        System.out.println(person);
    }
}

public String toString(){
    return "\nName: " + this.getName() + "\nPhone number: " + this.getTelephone();
}

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
