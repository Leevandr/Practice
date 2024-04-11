package eugene.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TaylorApproximation {

    // Метод для вычисления факториала
    private static int factorial(int n) {
        if (n == 0)
            return 1;
        else
            return n * factorial(n - 1);
    }

    // Метод для вычисления многочлена Тейлора
    public static List<Double> taylorPolynomial(double x0, int n, Function<Double, Double> f) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            result.add((f.apply(x0) * Math.pow(x0, i)) / (factorial(i)));
        }
        return result;
    }
}

