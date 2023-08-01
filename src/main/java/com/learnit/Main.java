package com.learnit;

import java.text.DecimalFormat;
import java.util.Random;

public class Main {
	public static void main(String[] args) {
		System.out.println(new DecimalFormat("000000").format(new Random().nextInt(999999)));
	}
}
