import java.util.*;

public class GestionReservas {

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Integer> logias7Personas = new ArrayList<>();
    private static final List<Integer> logias5Personas = new ArrayList<>();
    private static final List<Integer> logias3Personas = new ArrayList<>();
    private static final Map<String, String> reservas = new HashMap<>();  // Almacena "numeroLogia:capacidad"
    private static String matriculaActual = null;

    private static List<String> usuarios = new ArrayList<>();


    public static void mostrarDisponibilidadLogias(List<Integer> logias, String mensaje, int capacidadActual) {
        System.out.print(mensaje + " ");
        logias.stream()
                .filter(logia -> reservas.values().stream().noneMatch(reserva -> {
                    String[] partes = reserva.split(":");
                    int numeroLogia = Integer.parseInt(partes[0]);
                    int capacidad = Integer.parseInt(partes[1]);
                    return numeroLogia == logia && capacidad == capacidadActual;
                }))
                .forEach(logia -> System.out.print("Logia " + logia + " "));
        System.out.println();
    }

    public static void reservarLogia() {
        if (reservas.containsKey(matriculaActual)) {
            System.out.println("Ya tienes una logia reservada.");
            return;
        }

        List<Integer> logiasSeleccionadas = seleccionarLogias();
        if (logiasSeleccionadas == null) return;

        int numeroLogia = seleccionarNumeroDeLogia(logiasSeleccionadas);
        if (numeroLogia == -1) return;

        int capacidad = obtenerCapacidadLogiaPorLogiaSeleccionada(logiasSeleccionadas);
        if (solicitarIntegrantes(numeroLogia, capacidad)) {
            completarReserva(numeroLogia, capacidad);
        } else {
            System.out.println("La reserva fue cancelada.");
        }
    }

    public static void cancelarReserva() {
        if (!reservas.containsKey(matriculaActual)) {
            System.out.println("No tienes reservas para cancelar.");
        } else {
            String reserva = reservas.get(matriculaActual);
            String[] partes = reserva.split(":");
            int numeroLogia = Integer.parseInt(partes[0]);

            reservas.remove(matriculaActual);
            System.out.println("Reserva de la logia " + numeroLogia + " cancelada con éxito.");
        }
    }

     public static int solicitarOpcion(String mensaje, int min, int max) {
        while (true) {
            try {
                System.out.print(mensaje);
                int numero = scanner.nextInt();
                scanner.nextLine();
                if (numero >= min && numero <= max) {
                    return numero;
                } else {
                    System.out.println("Por favor, ingrese un número entre " + min + " y " + max + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número entero válido.");
                scanner.nextLine();
            }
        }
    }

    public static String limpiarMatricula(String matricula) {
        return matricula.replaceAll("[^\\dk]", "");
    }

    public static void consultarDisponibilidad() {
        mostrarDisponibilidadLogias(logias7Personas, "Logias de 7 personas disponibles:", 7);
        mostrarDisponibilidadLogias(logias5Personas, "Logias de 5 personas disponibles:", 5);
        mostrarDisponibilidadLogias(logias3Personas, "Logias de 3 personas disponibles:", 3);
    }

    public static void tamanoLogia(){
        System.out.println("\nSeleccione el tamaño de la logia a reservar:");
        System.out.println("1.- Logia para 7 personas");
        System.out.println("2.- Logia para 5 personas");
        System.out.println("3.- Logia para 3 personas");
        System.out.println("4.- Cancelar");
    }

    public static List<Integer> seleccionarLogias() {
        tamanoLogia();

        int opcion = solicitarOpcion("Ingrese una opción: ", 1, 4);
        if (opcion == 4) {
            System.out.println("Cancelando la reserva y volviendo al menú anterior...");
            return null;
        }

        return switch (opcion) {
            case 1 -> logias7Personas;
            case 2 -> logias5Personas;
            case 3 -> logias3Personas;
            default -> null;
        };
    }

    public static int seleccionarNumeroDeLogia(List<Integer> logiasSeleccionadas) {
        mostrarDisponibilidadLogias(logiasSeleccionadas, "\nLogias disponibles:", obtenerCapacidadLogiaPorLogiaSeleccionada(logiasSeleccionadas));
        int numeroLogia = solicitarNumeroLogia();

        if (!logiasSeleccionadas.contains(numeroLogia) || reservas.values().stream().anyMatch(reserva -> reserva.startsWith(numeroLogia + ":"))) {
            System.out.println("La logia seleccionada no está disponible.");
            return -1;
        }

        return numeroLogia;
    }

    public static int obtenerCapacidadLogiaPorLogiaSeleccionada(List<Integer> logiasSeleccionadas) {
        if (logiasSeleccionadas == logias7Personas) {
            return 7;
        } else if (logiasSeleccionadas == logias5Personas) {
            return 5;
        } else {
            return 3;
        }
    }

    public static boolean solicitarIntegrantes(int numeroLogia, int capacidad) {
        List<String> integrantes = new ArrayList<>();
        int cantidadmiembros = capacidad - 1;

        for (int i = 1; i <= cantidadmiembros; i++) {
            String matricula;
            while (true) {
                System.out.print("Matrícula del compañero " + i + " (ingrese 0 para cancelar): ");
                matricula = limpiarMatricula(scanner.nextLine());

                if (matricula.equals("0")) {
                    System.out.println("Cancelando la reserva de la logia " + numeroLogia + ".");
                    return false;
                } else if (matricula.equals(matriculaActual)) {
                    System.out.println("No puedes ingresar tu propia matrícula.");
                } else if (!usuarios.contains(matricula)) {
                    System.out.println("La matrícula " + matricula + " no está registrada.");
                } else if (integrantes.contains(matricula)) {
                    System.out.println("La matrícula " + matricula + " ya fue ingresada.");
                } else {
                    break;
                }
            }
            integrantes.add(matricula);
        }

        return true;
    }

    public static void completarReserva(int numeroLogia, int capacidad) {
        reservas.put(matriculaActual, numeroLogia + ":" + capacidad);
        System.out.println("Logia " + numeroLogia + " reservada con éxito.");
    }

    public static int solicitarNumeroLogia() {
        while (true) {
            try {
                System.out.print("Ingrese el número de logia: ");
                int numero = scanner.nextInt();
                scanner.nextLine();
                return numero;
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número entero válido.");
                scanner.nextLine();
            }
        }
    }

}
