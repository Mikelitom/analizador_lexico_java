import java.util.Scanner;

public class Menu {
  Scanner sc = new Scanner(System.in);

  public void run() {
    mainMenu();
  }

  public void message(String message) {
    System.out.println("---------------------------------");
    System.out.println(" -> " + message);
    System.out.println("---------------------------------");
  }

  public void mainMenu() {
    System.out.println("---------- Analizador -----------");
    System.out.print(" -> Ingrese su cadena: ");

    String str = sc.nextLine();

    message("Relizando analisis.");

    Analizer analizer = new Analizer();
    analizer.run(str);
    analizer.printTokens();
  }
}
