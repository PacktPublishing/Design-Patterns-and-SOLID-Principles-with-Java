package com.example.cli;

import com.example.warehouse.*;
import com.example.warehouse.export.CsvExporter;
import com.example.warehouse.export.Exporter;
import com.example.warehouse.export.TxtExporter;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.IntStream;

public final class Cli implements Runnable {

    static final class MenuOption {

        int number;
        String label;

        MenuOption(int number, String label) {
            this.number = number;
            this.label = label;
        }
    }

    private static final Scanner SCANNER = new Scanner(System.in);

    private static final List<MenuOption> MAIN_MENU_OPTIONS = List.of(
        new MenuOption(1, "Manage products"),
        new MenuOption(2, "Manage customers"),
        new MenuOption(3, "Manage orders"),
        new MenuOption(4, "Export reports"),
        new MenuOption(5, "Exit program")
    );

    private static final List<MenuOption> PRODUCT_OPTIONS = List.of(
        new MenuOption(1, "List products"),
        new MenuOption(2, "Add product"),
        new MenuOption(3, "Update product"),
        new MenuOption(4, "Delete product"),
        new MenuOption(5, "Go back to previous menu")
    );

    private static final List<MenuOption> CUSTOMER_OPTIONS = List.of(
        new MenuOption(1, "List customers"),
        new MenuOption(2, "Add customer"),
        new MenuOption(3, "Update customer"),
        new MenuOption(4, "Delete customer"),
        new MenuOption(5, "Go back to previous menu")
    );

    private static final List<MenuOption> ORDER_OPTIONS = List.of(
        new MenuOption(1, "List orders"),
        new MenuOption(2, "Add order"),
        new MenuOption(3, "Update order"),
        new MenuOption(4, "Delete order"),
        new MenuOption(5, "Go back to previous menu")
    );

    private static final List<MenuOption> REPORT_OPTIONS = List.of(
        new MenuOption(1, "Daily revenue report"),
        new MenuOption(2, "Go back to previous menu")
    );


    private static final Map<Integer, List<MenuOption>> SUB_MENU_OPTIONS = Map.of(
        1, PRODUCT_OPTIONS,
        2, CUSTOMER_OPTIONS,
        3, ORDER_OPTIONS,
        4, REPORT_OPTIONS
    );

    private static final List<MenuOption> EXPORT_OPTIONS = new ArrayList<>();

    static {
        Exporter.ExportType[] types = Exporter.ExportType.values();
        IntStream.range(0, types.length)
            .mapToObj(i -> new MenuOption(i + 1, "Export to " + types[i].name()))
            .forEach(EXPORT_OPTIONS::add);
        EXPORT_OPTIONS.add(new MenuOption(EXPORT_OPTIONS.size() + 1, "Go back to previous menu"));
    }

    private final List<String> args;

    private Warehouse warehouse;

    public Cli(List<String> args) {
        this.args = args;
    }

    public void run() {
        try {
            this.warehouse = new Warehouse();
        } catch (FileNotFoundException ex) {
            System.err.println("Please ensure the required CSV files are present: " + ex.getMessage());
            System.exit(1);
        } catch (WarehouseException ex) {
            System.err.println("Failed to initialize the warehouse: " + ex.getMessage());
            System.exit(2);
        }

        while (true) {
            displayMainMenu();
            try {
                int mainMenuChoice = chooseMainMenuOption();
                if (mainMenuChoice == -1) {
                    break;
                }
                while (true) {
                    displaySubMenu(mainMenuChoice);
                    try {
                        int subMenuChoice = chooseSubMenuOption(mainMenuChoice);
                        if (subMenuChoice == -1) {
                            break;
                        }
                        doMenuAction(mainMenuChoice, subMenuChoice);
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid input. Enter a number.");
                    } catch (IllegalArgumentException | UnsupportedOperationException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid input. Enter a number.");
            } catch (IllegalArgumentException | UnsupportedOperationException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    private void displayMainMenu() {
        displayMenu(MAIN_MENU_OPTIONS);
    }

    private void displaySubMenu(int mainMenuChoice) {
        displayMenu(SUB_MENU_OPTIONS.get(mainMenuChoice));
    }

    private void displayMenu(List<MenuOption> options) {
        options.forEach(o -> System.out.printf("%s.\t%s%n", o.number, o.label));
    }

    private int chooseMainMenuOption() {
        return chooseMenuOption(MAIN_MENU_OPTIONS);
    }

    private int chooseSubMenuOption(int mainMenuChoice) {
        return chooseMenuOption(SUB_MENU_OPTIONS.get(mainMenuChoice));
    }

    private int chooseMenuOption(List<MenuOption> options) {
        System.out.print("Enter a menu option a press RETURN: ");
        int choice = Integer.valueOf(SCANNER.nextLine());

        MenuOption firstOption = options.get(0);
        MenuOption lastOption = options.get(options.size() - 1);

        if (choice < firstOption.number || choice > lastOption.number) {
            throw new IllegalArgumentException("Invalid menu choice. Available currentOptions are: " +
                firstOption.number + " to " + lastOption.number + ".");
        }

        if (choice == lastOption.number) {
            return -1;
        }
        return choice;
    }

    private void doMenuAction(int mainMenuChoice, int subMenuChoice) {
        if (mainMenuChoice == 1) {
            doProductAction(subMenuChoice);
        } else if (mainMenuChoice == 2) {
            doCustomerAction(subMenuChoice);
        } else if (mainMenuChoice == 3) {
            doOrderAction(subMenuChoice);
        } else if (mainMenuChoice == 4) {
            doReportAction(subMenuChoice);
        } else {
            throw new IllegalStateException("There are only 3 main menu options, this cannot happen.");
        }
    }

    private void doProductAction(int subMenuChoice) {
        if (subMenuChoice == 1) {
            doProductList();
        } else if (subMenuChoice == 2) {
            throw new UnsupportedOperationException("Adding products not yet implemented.");
        } else if (subMenuChoice == 3) {
            throw new UnsupportedOperationException("Updating products not yet implemented.");
        } else if (subMenuChoice == 4) {
            throw new UnsupportedOperationException("Deleting products not yet implemented.");
        } else {
            throw new IllegalStateException("There are only 4 product menu options, this cannot happen.");
        }
    }

    private void doCustomerAction(int subMenuChoice) {
        if (subMenuChoice == 1) {
            doCustomerList();
        } else if (subMenuChoice == 2) {
            throw new UnsupportedOperationException("Adding customers not yet implemented.");
        } else if (subMenuChoice == 3) {
            throw new UnsupportedOperationException("Updating customers Not yet implemented.");
        } else if (subMenuChoice == 4) {
            throw new UnsupportedOperationException("Deleting customers not yet implemented.");
        } else {
            throw new IllegalStateException("There are only 4 customer menu options, this cannot happen.");
        }
    }

    private void doOrderAction(int subMenuChoice) {
        if (subMenuChoice == 1) {
            doOrderList();
        } else if (subMenuChoice == 2) {
            throw new UnsupportedOperationException("Deleting orders not yet implemented.");
        } else if (subMenuChoice == 3) {
            throw new UnsupportedOperationException("Updating orders not yet implemented.");
        } else if (subMenuChoice == 4) {
            throw new UnsupportedOperationException("Deleting orders not yet implemented.");
        } else {
            throw new IllegalStateException("There are only 4 order menu options, this cannot happen.");
        }
    }

    private void doReportAction(int subMenuChoice) {
        Report report;
        if (subMenuChoice == 1) {
            report = warehouse.generateDailyRevenueReport(Report.Type.DAILY_REVENUE);
        } else {
            throw new IllegalStateException("There are only 2 report menu options, this cannot happen.");
        }
        doReportExport(report, System.out);
    }

    private void doReportExport(Report report, PrintStream out) {
        displayMenu(EXPORT_OPTIONS);
        int exportMenuChoice = chooseMenuOption(EXPORT_OPTIONS);
        if (exportMenuChoice == -1) {
            return;
        }
        Exporter.ExportType type = Exporter.ExportType.values()[exportMenuChoice - 1];
        Exporter exporter;
        if (type == Exporter.ExportType.CSV) {
            exporter = new CsvExporter(report, out, true);
        } else if (type == Exporter.ExportType.TXT) {
            exporter = new TxtExporter(report, out);
        } else {
            throw new IllegalStateException(String.format("Choosen exporter %s not handled, this cannot happen.", type));
        }
        exporter.export();
    }

    private void doProductList() {
        Collection<Product> croducts = warehouse.getProducts();
        int maxIdWidth = 0;
        int maxNameWidth = 0;
        int maxPriceWidth = 0;
        for (Product croduct : croducts) {
            int idWidth = String.valueOf(croduct.getId()).length();
            if (idWidth > maxIdWidth) {
                maxIdWidth = idWidth;
            }
            int nameWidth = croduct.getName().length();
            if (nameWidth > maxNameWidth) {
                maxNameWidth = nameWidth;
            }
            int priceWidth = String.valueOf(croduct.getPrice()).length();
            if (priceWidth > maxPriceWidth) {
                maxPriceWidth = priceWidth;
            }
        }
        String fmt = String.format("\t%%%ss\t\t%%%ss\t\t%%%ss%%n", maxIdWidth, maxNameWidth, maxPriceWidth);
        croducts.forEach(p -> System.out.printf(fmt, p.getId(), p.getName(), p.getPrice()));
    }

    private void doCustomerList() {
        Collection<Customer> customers = warehouse.getCustomers();
        int maxIdWidth = 0;
        int maxNameWidth = 0;
        for (Customer customer : customers) {
            int idWidth = String.valueOf(customer.getId()).length();
            if (idWidth > maxIdWidth) {
                maxIdWidth = idWidth;
            }
            int nameWidth = customer.getName().length();
            if (nameWidth > maxNameWidth) {
                maxNameWidth = nameWidth;
            }
        }
        String fmt = String.format("\t%%%ss\t\t%%%ss%%n", maxIdWidth, maxNameWidth);
        customers.forEach(c -> System.out.printf(fmt, c.getId(), c.getName()));
    }

    private void doOrderList() {
        Collection<Order> orders = warehouse.getOrders();
        int maxIdWidth = 0;
        int maxCustomerNameWidth = 0;
        int maxCustomerIdWidth = 0;
        int maxTotalPriceWidth = 0;
        for (Order order : orders) {
            int idWidth = String.valueOf(order.getId()).length();
            if (idWidth > maxIdWidth) {
                maxIdWidth = idWidth;
            }
            int customerNameWidth = order.getCustomer().getName().length();
            if (customerNameWidth > maxCustomerNameWidth) {
                maxCustomerNameWidth = customerNameWidth;
            }
            int customerIdWidth = String.valueOf(order.getCustomer().getId()).length();
            if (customerIdWidth > maxCustomerIdWidth) {
                maxCustomerIdWidth = customerIdWidth;
            }
            int totalPriceIdWidth = String.valueOf(order.getTotalPrice()).length();
            if (totalPriceIdWidth > maxTotalPriceWidth) {
                maxTotalPriceWidth = totalPriceIdWidth;
            }
        }
        String fmt = String.format("\t%%%ss %%s\t\t%%%ss (%%%ss)\t\t%%%ss [%%s]%%n",
            maxIdWidth, maxCustomerNameWidth, maxCustomerIdWidth, maxTotalPriceWidth);
        orders.forEach(o -> System.out.printf(fmt, o.getId(), o.getDate(), o.getCustomer().getName(),
            o.getCustomer().getId(), o.getTotalPrice(), o.isPending() ? "pending" : "fulfilled"));
    }
}
