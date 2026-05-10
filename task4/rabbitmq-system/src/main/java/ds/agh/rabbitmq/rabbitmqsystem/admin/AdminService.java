package ds.agh.rabbitmq.rabbitmqsystem.admin;

import ds.agh.rabbitmq.rabbitmqsystem.message.AdminMessage;
import ds.agh.rabbitmq.rabbitmqsystem.message.Command;
import ds.agh.rabbitmq.rabbitmqsystem.message.GoodType;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
@Service
@Profile("admin")
public class AdminService implements CommandLineRunner {
    private final AdminSender sender;
    private final MessagesStore store;

    public AdminService(AdminSender sender, MessagesStore store) {
        this.sender = sender;
        this.store = store;
    }

    private void printMenu() {
        System.out.println("Available commands:");
        System.out.println("/h - Help");
        System.out.println("/history <n> - Show last n messages");
        System.out.println("/carriers <message> - Send text message to all carriers");
        System.out.println("/carriers --start - Resume carriers");
        System.out.println("/carriers --stop - Stop carriers");
        System.out.println("/order <goods> <quantity> - Trigger order from agencies (goods: PEOPLE, CARGO, SATELLITE)");
        System.out.println("/agencies <message> - Send text message to all agencies");
        System.out.println("/agencies --start - Resume agencies");
        System.out.println("/agencies --stop - Stop agencies");
        System.out.println("/all <message> - Send text message to all");
        System.out.println("exit - Exit the application");
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        printMenu();
        while (true) {
            String input = br.readLine();
            if (input == null || "exit".equalsIgnoreCase(input)) {
                break;
            }
            if (input.isBlank()) continue;

            String[] parts = input.split(" ", 2);
            String command = parts[0];
            String arg = parts.length > 1 ? parts[1] : "";

            try {
                switch (command) {
                    case "/h" -> printMenu();
                    case "/history" -> {
                        int n = arg.isBlank() ? Integer.MAX_VALUE : Integer.parseInt(arg);
                        var msgs = store.getMessages();
                        int start = Math.max(0, msgs.size() - n);
                        for (int i = start; i < msgs.size(); i++) {
                            System.out.println(msgs.get(i));
                        }
                    }
                    case "/carriers" -> {
                        if (arg.equalsIgnoreCase("--stop")) {
                            sender.sendAdminMessage(new AdminMessage(Command.STOP, null), "admin.carrier");
                        } else if (arg.equalsIgnoreCase("--start")) {
                            sender.sendAdminMessage(new AdminMessage(Command.RESUME, null), "admin.carrier");
                        } else {
                            sender.sendAdminMessage(new AdminMessage(Command.INFO, arg), "admin.carrier");
                        }
                    }
                    case "/agencies" -> {
                        if (arg.equalsIgnoreCase("--stop")) {
                            sender.sendAdminMessage(new AdminMessage(Command.STOP, null), "admin.agency");
                        } else if (arg.equalsIgnoreCase("--start")) {
                            sender.sendAdminMessage(new AdminMessage(Command.RESUME, null), "admin.agency");
                        } else {
                            sender.sendAdminMessage(new AdminMessage(Command.INFO, arg), "admin.agency");
                        }
                    }
                    case "/order" -> {
                        String[] orderParts = arg.split(" ");
                        if (orderParts.length < 2) {
                            System.out.println("Usage: /order <PEOPLE|CARGO|SATELLITE> <quantity>");
                            continue;
                        }
                        GoodType type = GoodType.valueOf(orderParts[0].toUpperCase());
                        double quantity = Double.parseDouble(orderParts[1]);
                        sender.sendAdminMessage(new AdminMessage(Command.ORDER, type, quantity), "admin.agency");
                    }
                    case "/all" -> sender.sendAdminMessage(new AdminMessage(Command.INFO, arg), "admin.all");
                    default -> System.out.println("Unknown command. Type /h for help.");
                }
            } catch (Exception e) {
                System.out.println("Error processing command: " + e.getMessage());
            }
        }
        br.close();
    }
}
