package pl.com.bottega.photostock.sales.application;

import pl.com.bottega.photostock.sales.model.Clip;
import pl.com.bottega.photostock.sales.model.Picture;
import pl.com.bottega.photostock.sales.model.money.Money;

import java.util.HashSet;

public class PictureEqualsTest {

    public static void main(String[] args) {
        Picture product1 = picture("123");
        Picture product2 = picture(null);
        Picture product3 = picture("123");
        Picture product4 = picture("066");
        Picture product5 = picture(null);

        Clip clip1 = clip("123");
        Clip clip2 = clip("ABC");
        Clip clip3 = clip("123");

        System.out.println("Possitive:");
        System.out.println(clip1.equals(clip3));
        System.out.println(product1.equals(product3));
        System.out.println(product1.equals(product1));
        System.out.println(product1.equals(product1));
        System.out.println(product2.equals(product2));
        System.out.println(product2.equals(product5));

        System.out.println("Negative:");
        System.out.println(clip1.equals(clip2));
        System.out.println(clip2.equals(product1));
        System.out.println(product1.equals(product2));
        System.out.println(product3.equals(product5));
        System.out.println(product5.equals(null));
        System.out.println(product1.equals(12));
    }

    private static Picture picture(String number) {
        return new Picture(number, "", new HashSet<>(), Money.valueOf(100));
    }

    private static Clip clip(String number){
        return new Clip("", number, 500l, Money.valueOf(0));
    }
}
