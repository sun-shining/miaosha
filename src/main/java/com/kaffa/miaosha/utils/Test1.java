package com.kaffa.miaosha.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Test1 {
    public static Set<Integer> sets = new HashSet<>();
    public static StringBuffer  sb = new StringBuffer();

    public static void main(String[] args) {
        char[] ops = new char[]{'+', '-', '*', '/'};
        int a[] = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rdm = new Random();

        while (true) {
            int sum1 = cal2IntValue( a[genIndex(a)], a[genIndex(a)],ops[rdm.nextInt(4)]);
            int sum2 = cal2IntValue(sum1, a[genIndex(a)], ops[rdm.nextInt(4)]);
            int sum3 = cal2IntValue(sum2, a[genIndex(a)], ops[rdm.nextInt(4)]);
            int sum4 = cal2IntValue(sum3, a[genIndex(a)], ops[rdm.nextInt(4)]);
            int sum5 = cal2IntValue(sum4, a[genIndex(a)], ops[rdm.nextInt(4)]);
            int sum6 = cal2IntValue(sum5, a[genIndex(a)], ops[rdm.nextInt(4)]);
            int sum7 = cal2IntValue(sum6, a[genIndex(a)], ops[rdm.nextInt(4)]);
            int sum = cal2IntValue(sum7, a[genIndex(a)], ops[rdm.nextInt(4)]);
            if (sum != 100) sb = new StringBuffer();
            if (sum == 100){
                System.err.println(sum);
                break;
            }
        }
        System.out.println(sb.toString());
    }

    private static int genIndex(int[] a) {
        int index = (int)(Math.random()*a.length);
        while (true){
            if (sets.size() ==9){
                sets.clear();
                break;
            }

            index=(int)(Math.random()*a.length);
            if (sets.add(new Integer(index))){//没有返回true
                break;
            }
        }
        sets.add(new Integer(index));
        return index;
    }

    private static int cal2IntValue(int a, int b, char op1) {
        sb.append(a).append(op1).append(b).append(" $$ ");
        switch (op1) {
            case '+':
                return a+b;
            case '-':
                return a-b;
            case '*':
                return a*b;
            case '/':
                return a/b;
        }
        return -1000000000;
    }
}
