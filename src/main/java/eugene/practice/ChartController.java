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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Function;

@Controller
public class ChartController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/chart")
    public String generateChart(@RequestParam int n,
                                @RequestParam double epsilon,
                                @RequestParam double x0,
                                @RequestParam double a,
                                @RequestParam double b,
                                Model model) throws FileNotFoundException {
        Function<Double, Double> f = Math::sin; // Пример функции, замените на нужную

        // Создаем набор данных для графика
        XYSeries series = new XYSeries("Функция");
        XYSeries approxSeries = new XYSeries("Ряд Томпсона");

        // Генерируем точки для функции и аппроксимации
        double step = 0.01; // Шаг для генерации точек
        for (double x = a; x <= b; x += step) {
            double functionValue = f.apply(x);
            double approxValue = TaylorApproximation.taylorPolynomial(x0, n, f).get(0); // Пример аппроксимации, замените на вашу функцию аппроксимации
            // Проверяем условие эпсилон
            if (Math.abs(functionValue - approxValue) <= epsilon) {
                series.add(x, functionValue);
                approxSeries.add(x, approxValue);
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(approxSeries);

        // Создаем график
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Аппроксимация методом рядов Томпсона",
                "x",
                "y",
                dataset
        );

        // Настраиваем стиль графика
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE); // Цвет для функции
        chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED); // Цвет для аппроксимации


        String name = "chart.png";
        File dir = ResourceUtils.getFile("classpath:static/files/");
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("Не удалось создать директорию: " + dir.getAbsolutePath());
            }
        }
        File imageFile = new File(dir + "/" + name);
        try {
            ChartUtils.saveChartAsPNG(imageFile, chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Возвращаем имя представления и добавляем атрибуты для отображения данных на странице
        model.addAttribute("chartName", "Аппроксимация методом рядов Томпсона");

        return "chart";
    }

}

