package Lesson5.src;

public class TreadMas {
    static final int size = 10000000;
    static final int h = size/2;
    static float[] arr = new float[size];
    static long c;
    static long b;
    static float[] arr1 = new float[h];
    static float[] arr2 = new float[h];

    private static void oneMas(float[] Array){
        for(int i = 0; i < Array.length; i++){
            arr[i] = 1;
        }

    }

    public static void main (String[] args){
        oneMas(arr);
        // Определяе сколько времени понадобилось, чтоб рассчитать весь массив
        c = System.currentTimeMillis();
        calculation(arr);
        b = System.currentTimeMillis();
        System.out.println("Необходимое время для рассчета массива 1 методом: "  + (b - c));

        // Разбивка основного массива на два
        long start_smashArr = System.currentTimeMillis();
        smashArr();

        // запускаем расчеты массивов по разным потокам
        Thread thread1 = new Thread(() -> calculation(arr1));
        Thread thread2 = new Thread(() -> calculation(arr2));
        thread1.start();
        thread2.start();

        // Склеиваем массивы
        connectArr();
        long end_connectArr = System.currentTimeMillis();

        System.out.println("Необходимое время для рассчета массива 2 методом: " + (end_connectArr - start_smashArr));
    }

    private static void calculation(float[] Array){
        for (int i = 0; i < Array.length; i++){
            Array[i] = (float)(Array[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
    }

    private static void smashArr(){
        System.arraycopy(arr, 0, arr1, 0, h);
        System.arraycopy(arr, h, arr2, 0, h);
    }

    private static void connectArr(){
        System.arraycopy(arr1, 0, arr, 0, h);
        System.arraycopy(arr2, 0, arr, h, h);
    }
}
