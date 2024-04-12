package eugene.practice;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

@Controller
public class ChartController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/chart")
    public String generateChart(@RequestParam int degree,
                                @RequestParam double epsilon,
                                @RequestParam double x0,
                                @RequestParam double a,
                                @RequestParam double b,
                                Model model) {
        // Используем функцию синуса
        Function<Double, Double> function = Math::sin;

        XYSeries series = new XYSeries("Функция");
        XYSeries approxSeries = new XYSeries("Ряд Тейлора");

        double step = 0.001;
        for (double x = a; x <= b; x += step) {
            double functionValue = function.apply(x);
            // Вычисляем значение многочлена Тейлора с использованием функции синуса и точки x0
            double approxValue = calculateTaylorPolynomial(x, x0, degree, function);
            series.add(x, functionValue);
            approxSeries.add(x, approxValue);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(approxSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Аппроксимация методом рядов Тейлора",
                "x",
                "y",
                dataset
        );

        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);

        String name = "chart.png";
        try {
            File dir = ResourceUtils.getFile("classpath:static/files/");
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + dir.getAbsolutePath());
            }
            File imageFile = new File(dir + "/" + name);
            ChartUtils.saveChartAsPNG(imageFile, chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("chartName", "Аппроксимация методом рядов Тейлора");

        return "chart";
    }

    private double calculateTaylorPolynomial(double x, double x0, int degree, Function<Double, Double> function) {
        double result = 0;
        for (int i = 0; i <= degree; i++) {
            double term = function.apply(x) * Math.pow(x - x0, i) / factorial(i);
            result += term;
        }
        return result;
    }


    private int factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Факториал не определен для отрицательных чисел.");
        }
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
