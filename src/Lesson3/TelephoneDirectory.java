package Lesson3;

public class TelephoneDirectory {
    public static void main(String[] args){
        Person person1 = new Person("Вертяев", "458796");
        Person person2 = new Person("Шишкин", "124763");
        Person person3 = new Person("Путеева", "367985");
        Person person4 = new Person("Лапушкин", "167942");
        Person person5 = new Person("Меркулова", "368952");
        Person person6 = new Person("Воронков", "817245");
        Person person7 = new Person("Трюкин", "395842");
        Person person8 = new Person("Лапушкин", "123489");

//        Person.printAll();
        Person.findPerson("Лапушкин");
    }
}
