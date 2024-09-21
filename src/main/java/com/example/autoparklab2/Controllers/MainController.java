package com.example.autoparklab2.Controllers;

import com.example.autoparklab2.Models.Cars;
import com.example.autoparklab2.repository.CarsRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {
    private final CarsRepository carsRepository;

    public MainController(CarsRepository carsRepository) {
        this.carsRepository = carsRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная страница");
        return "greeting";
    }

    @GetMapping("/autopark")
    public String autopark(Model model) {
        Iterable<Cars> cars = carsRepository.findAll();
        model.addAttribute("cars", cars);
        model.addAttribute("title", "Автопарк");
        return "autopark";
    }

    @GetMapping("/support")
    public String support(Model model) {
        model.addAttribute("title", "Поддержка");
        return "support";
    }

    @GetMapping("/addcar")
    public String addcar(Model model) {
        model.addAttribute("title", "Добавить машину");
        return "addcar";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Страница входа");
        return "login";
    }

    @GetMapping("/autopark/{id}")
    public String autopark(Model model, @PathVariable long id) {
        if (!carsRepository.existsById(id)){
            return "redirect:/autopark";
        }
        Optional<Cars> cars = carsRepository.findById(id);
        ArrayList<Cars> res = new ArrayList<>();
        cars.ifPresent(res::add);
        model.addAttribute("cars", res);
        return "carDetails";
    }

    @GetMapping("/autopark/stats")
    public String stats(Model model) {
        List<Object[]> stats = carsRepository.findCarIssueStats();

        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (Object[] row : stats) {
            dates.add(row[0].toString());
            counts.add((Long) row[1]);
        }

        model.addAttribute("dates", dates);
        model.addAttribute("counts", counts);
        return "car_stats";
    }






    @PreAuthorize("isAuthenticated()")
    @PostMapping("/addcar")
    public String addcarPost(@RequestParam String brand,
                             @RequestParam int manufacture_year,
                             @RequestParam LocalDate registration_date,
                             @RequestParam String full_name,
                             Model model) {
        Cars cars = new Cars(brand, manufacture_year, registration_date, full_name);
        carsRepository.save(cars);
        return "redirect:/autopark";
    }

//    @PostMapping("/autopark/{id}")
//    public String autoparkPost(@PathVariable("id") long id,
//                                @RequestParam String brand,
//                                @RequestParam int manufacture_year,
//                                @RequestParam LocalDate registration_date,
//                                @RequestParam String full_name,
//                                Model model) {
//        Cars car = carsRepository.findById(id).orElseThrow();
//        car.setBrand(brand);
//        car.setManufacture_year(manufacture_year);
//        car.setRegistration_date(registration_date);
//        car.setFull_name(full_name);
//        carsRepository.save(car);
//        return "redirect:/autopark";
//    }

    @PostMapping("/autopark/{id}/remove")
    public String removeCar(@PathVariable("id") long id) {
        Cars car = carsRepository.findById(id).orElseThrow();
        carsRepository.delete(car);
        return "redirect:/autopark";
    }

    @GetMapping("/autopark/filter")
    public String searchCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer manufacture_year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registration_date,
            @RequestParam(required = false) String full_name,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            Model model) {


        Sort.Direction sortDirection = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortBy = Sort.by(sortDirection, "registration_date");


        List<Cars> cars;
        if (brand != null || manufacture_year != null || registration_date != null || full_name != null) {
            cars = carsRepository.findByParams(brand, manufacture_year, registration_date, full_name, sortBy);
        } else {
            cars = carsRepository.findAll(sortBy);
        }


        model.addAttribute("cars", cars);
        return "autopark"; // Имя шаблона для отображения
    }

    @PostMapping("/autopark/save")
    public String carEdit(
            @RequestParam("id") long id,
            @RequestParam String brand,
            @RequestParam int manufacture_year,
            @RequestParam LocalDate registration_date,
            @RequestParam String full_name,
            Model model) {

        Cars car = carsRepository.findById(id).orElseThrow();
        car.setBrand(brand);
        car.setManufacture_year(manufacture_year);
        car.setRegistration_date(registration_date);
        car.setFull_name(full_name);

        carsRepository.save(car);

        return "redirect:/autopark";
    }


}
