package com.imagepicker.utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Scanner;

/**
 * author by Anuj Sharma on 10/16/2017.
 */

public class SingletonClass implements Serializable {
    private static SingletonClass instance;
    private static SingletonClass instance2;

    public static synchronized SingletonClass getInstance() {
        if (null == instance) {
            synchronized (SingletonClass.class) {
                if (null == instance) {
                    instance = new SingletonClass();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
//        getInstance().getInstanceObj();
//        findFactorial(5);
//        findFibonnaci(10);
        Scanner in = new Scanner(System.in);
        int t=in.nextInt();
        for(int i=0;i<t;i++){
            int a = in.nextInt();
            int b = in.nextInt();
            int n = in.nextInt();

            int sum = 0;
            for(int j=0;j<n;j++){
                if(j==0){
                    sum = (int) (a + (Math.pow(2,j)*b));
                }else{
                    sum += (int) (Math.pow(2,j)*b);
                }
                System.out.print(sum + " ");
            }
            System.out.println("");
        }
        in.close();

        /*System.out.println("================================");
        Scanner sc=new Scanner(System.in);
        for(int i=0;i<3;i++){
            String s1=sc.next();
            int x=sc.nextInt();
            //Complete this line
            StringBuilder stringX = new StringBuilder(String.valueOf(x));
            StringBuilder finaalX = new StringBuilder();
            if(stringX.toString().length()<3){
                if(stringX.toString().length()==1){
                    finaalX.append("00");
                }else if(stringX.toString().length()==2){
                    finaalX.append("0");
                }
                finaalX.append(stringX);

            }else{
                finaalX.append(x);
            }
            System.out.printf( "%-15s %5s %n", s1,finaalX);
//            System.out.print(s1 + "");
//            System.out.print("               "+finaalX);
//            System.out.println("");
        }
        System.out.println("================================");
*/
//        1 1 1 0 0 0
//        0 1 0 0 0 0
//        1 1 1 0 0 0
//        0 0 2 4 4 0
//        0 0 0 2 0 0
//        0 0 1 2 4 0





        /*Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int k = in.nextInt();
        int a[] = new int[n];
        for(int a_i=0; a_i < n; a_i++){
            a[a_i] = in.nextInt();
        }

        int[] output = new int[n];

        output = arrayLeftRotation(a, n, k);
        for(int i = 0; i < n; i++)
            System.out.print(output[i] + " ");

        System.out.println();*/
    }

    protected Object readResolve() {
        return instance;
    }

    //using reflection
    void getInstanceObj() {
        try {
            Constructor[] constructors = SingletonClass.class.getConstructors();
            for (Constructor con :
                    constructors) {
                con.setAccessible(true);
                instance2 = (SingletonClass) con.newInstance();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("instance1" + instance.hashCode());
        System.out.println("instance2" + instance2.hashCode());
        String a = "anuj";
        String b = "nuja";
        char[] charA = a.toLowerCase().toCharArray();
        char[] charB = b.toLowerCase().toCharArray();

        Arrays.sort(charA);
        Arrays.sort(charB);

        if (charA.equals(charB)) {
            System.out.println("values match");
        } else {
            System.out.println("values do not match");
        }

        /*if (a.indexOf(b) != -1) {
            System.out.println("true hahaha");
        } else {
            System.out.println("false nanana");
        }

        if (a.contains(b)) {
            System.out.println("true1 hahaha");
        } else {
            System.out.println("false1 nanana");
        }*/
    }

    public static int[] arrayLeftRotation(int[] a, int n, int k) {
        if (a != null && a.length > 1) {
            for (int i = 0; i < k; i++) {
                int j, temp = 0;
                temp = a[0];
                for (j = 0; j < n - 1; j++) {
                    a[j] = a[j + 1];
                }
                a[j] = temp;
            }
        }
        return a;
    }

    public static void findFactorial(int x) {
        int temp = x;
        if (x > 0) {
            for (int i = x; i > 1; i--) {
                temp = temp * (i - 1);
                System.out.println(" -> " + temp);
            }
            System.out.println("factorial of " + x + " is " + temp);
        }
    }

    public static void findFibonnaci(int x) {
        int temp = 0;
        int a = 0, b = 1;
        if (x > 1) {
//            System.out.print("" + b);
            for (int i = 1; i <= x; i++) {
                a = b;
                b = temp;
                temp = a + b;
                System.out.print(" " + temp);
            }
        }else{
            System.out.println(a + ""+ b);
        }
    }

    public void reverseArray(int[] a, int x){
        int b[] = new int[a.length];

        for (int i = a.length; i >=0; i--) {
            b[i] = a[1];
        }
    }
}
