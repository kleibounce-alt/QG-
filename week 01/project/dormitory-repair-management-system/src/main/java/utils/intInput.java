package utils;

import java.util.Scanner;

public class intInput {
    //封装重复输入
    public static int intInput(int l, int r) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                int choice = sc.nextInt();
                if (choice >= l && choice <= r) {
                    return choice;
                }
                else {
                    System.out.println("请输入(" + l + " ~ " + r + ")之间的整数！");
                }
            }
            catch (Exception e) {
                System.out.println("输入格式错误！请重新输入！");
                sc.next();
            }
        }
    }
}
