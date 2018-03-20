package com.kaffa.miaosha.utils;
//123456789  插入+-*/后结果等于100
public class Test {
    public static void main(String[] args) {
        char op, str[] = new char[80];
        int i, j, s, n, m, ptr;
        for (i = 0; i <= 6561; i++) {
            s = 0;
            m = 1;
            n = i;
            op = '+';
            ptr = 0;
            str[ptr++] = '1';
            for (j = 2; j <= 9; j++) {
                if (n % 3 != 0) {
                    if (op == '+')
                        s += m;
                    else
                        s -= m;
                    m = j;
                }
                switch (n % 3) {
                    case 0:
                        m = m * 10 + j;
                        break;
                    case 1:
                        op = '+';
                        break;
                    case 2:
                        op = '-';
                        break;
                }
                if (n % 3 != 0)
                    str[ptr++] = op;
                str[ptr++] =  (char) ('0' + j);
                n /= 3;
            }
            if (op == '+')
                s += m;
            else
                s -= m;
            str[ptr] = '\0';
            if (s == 100) {
                //因为java中char类型数组默认的toString方法是输出对象索引地址，所以做如下截取输出
                int l = String.valueOf(str).trim().indexOf(0);
                if (l==-1) {
                    System.out.printf("FOUND: %s=%d\n", String.valueOf(str).trim(), s);
                } else {
                    System.out.printf("FOUND: %s=%d\n", new String(str).substring(0, l), s);
                }
            }
        }
    }
}