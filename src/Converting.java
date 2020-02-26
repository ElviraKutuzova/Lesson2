import java.util.Arrays;
import java.util.Random;



public class Converting {
    private static int size = 4;
    private static String[][] stringArr = {{"5","l","7","8"}, {"9","10","11","12"}, {"11","10","9","8"}, {"7","6","5","4"}};
    private static String[][] stringArr2 = {{"5","6","7","8"}, {"9","10","11","12"}, {"11","10","9","8"}, {"7","6","5","4"}, {"9","10","11","12"}};
    private static String[][] stringArr3 = {{"5","6","7","l"}, {"9","10","11","12"}, {"11","10","9","8"}, {"7","6","5","4"}, {"9","10","11","12"}};

    public static void main (String[]args){
        //iniMap();
        String[][] a = stringArr3;
        System.out.println("Исходный массив: " + Arrays.deepToString(a));
        try {
        proverLength(a.length);
        }catch (MyArraySizeException e){
            System.out.println("Массив слишком велик!");

        }
        System.out.println("Преобразованный массив: ");
        try {
            accept(a);
        }catch (MyArrayDataException e){
            System.out.println("Элемент массива невозможно преобразовать в числовое значение!");
        }
    }


    private static void proverLength(int b) throws MyArraySizeException {
        if (b > size) {
                throw new MyArraySizeException();
            }
    }

    private static void exInt(int i, int j)  {
        System.out.println("\n" + "Ячейка: " +  i + ":" + j );
    }


    private static void accept(String[][] arr) {
        int total = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                try {
                    Integer map3 = Integer.valueOf(arr[i][j]);
                    total += map3;
                    System.out.print(map3 + " ");
                } catch (NumberFormatException map3) {
                    exInt(i, j);
                    throw new MyArrayDataException();
                }
            }
                System.out.println();
        }
           System.out.println("Сумма всех элементов массива равна: " + total);
    }
}