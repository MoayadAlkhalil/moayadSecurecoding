package fainaltest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

 class Person {
    String fullName;
    String phoneNumber;
    int personAge;
    String personGender;

    Person(String fullName, String phoneNumber, int personAge, String personGender) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.personAge = personAge;
        this.personGender = personGender;
    }

    void displayDetails() {
        System.out.println("Full Name: " + fullName);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Age: " + personAge);
        System.out.println("Gender: " + personGender);
    }
}

class Doctor extends Person {
    private byte[] passwordHash;

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPassword(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    Doctor(String fullName, String phoneNumber, int personAge, String personGender, byte[] passwordHash) {
        super(fullName, phoneNumber, personAge, personGender);
        this.passwordHash = passwordHash;
    }

    boolean authenticate(String enteredPassword) {
        byte[] enteredPasswordHash = Hash.getHash(enteredPassword);
        return MessageDigest.isEqual(enteredPasswordHash, passwordHash);
    }

    void displayDetails() {
        super.displayDetails();
    }

    void recordMedicalInformation(Patient patient, String medicalCondition, String medicalTreatment) {
        System.out.println("Doctor: " + fullName);
        System.out.println("Recording medical information for patient: " + patient.fullName);
        System.out.println("Medical Condition: " + medicalCondition);
        System.out.println("Medical Treatment: " + medicalTreatment);

        // Save medical information to file
        saveMedicalInformation(patient.fullName, medicalCondition, medicalTreatment);
    }

    private void saveMedicalInformation(String patientFullName, String medicalCondition, String medicalTreatment) {
        String filePath = "C:\\Users\\moaed\\eclipse-workspace\\secure code\\src\\fainal1\\medical_information.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(patientFullName + "," + medicalCondition + "," + medicalTreatment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Patient extends Person {
    private byte[] passwordHash;

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPassword(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    Patient(String fullName, String phoneNumber, int personAge, String personGender, byte[] passwordHash) {
        super(fullName, phoneNumber, personAge, personGender);
        this.passwordHash = passwordHash;
    }

    boolean authenticate(String enteredPassword) {
        byte[] enteredPasswordHash = Hash.getHash(enteredPassword);
        return MessageDigest.isEqual(enteredPasswordHash, passwordHash);
    }

    void viewMedicalHistory() {
        System.out.println("Patient: " + fullName);
        System.out.println("Viewing medical history...");

        // Load and display medical information from file
        loadAndDisplayMedicalInformation(fullName);
    }

     void loadAndDisplayMedicalInformation(String patientFullName) {
        String filePath = "C:\\Users\\moaed\\eclipse-workspace\\secure code\\src\\fainal1\\medical_information.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String storedPatientFullName = parts[0];
                String medicalCondition = parts[1];
                String medicalTreatment = parts[2];

                if (storedPatientFullName.equals(patientFullName)) {
                    System.out.println("Medical Condition: " + medicalCondition);
                    System.out.println("Medical Treatment: " + medicalTreatment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class HealthSystem {
    private static final String DOCTORS_FILE = "C:\\Users\\moaed\\eclipse-workspace\\secure code\\src\\fainal1\\doctors.txt";
    private static final String PATIENTS_FILE = "C:\\Users\\moaed\\eclipse-workspace\\secure code\\src\\fainal1\\patients.txt";
    private static final String MEDICAL_INFO_FILE = "C:\\Users\\moaed\\eclipse-workspace\\secure code\\src\\fainal1\\medical_information.txt";
    static final int MAX_LOGIN_ATTEMPTS = 3;

    Map<String, Doctor> doctors = new HashMap<>();
    Map<String, Patient> patients = new HashMap<>();
    Map<String, Integer> loginAttempts = new HashMap<>();

    HealthSystem() {
        initializeFiles();
        loadDoctors();
        loadPatients();
    }

    private void initializeFiles() {
        try {
            File doctorsFile = new File(DOCTORS_FILE);
            if (!doctorsFile.exists()) {
                doctorsFile.createNewFile();
            }

            File patientsFile = new File(PATIENTS_FILE);
            if (!patientsFile.exists()) {
                patientsFile.createNewFile();
            }

            File medicalInfoFile = new File(MEDICAL_INFO_FILE);
            if (!medicalInfoFile.exists()) {
                medicalInfoFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDoctors() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DOCTORS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String phoneNumber = parts[0];
                String fullName = parts[1];
                int personAge = Integer.parseInt(parts[2]);
                String personGender = parts[3];
                byte[] passwordHash = Base64.getDecoder().decode(parts[4]);
                doctors.put(fullName, new Doctor(fullName, phoneNumber, personAge, personGender, passwordHash));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPatients() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATIENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String phoneNumber = parts[0];
                String fullName = parts[1];
                int personAge = Integer.parseInt(parts[2]);
                String personGender = parts[3];
                byte[] passwordHash = Base64.getDecoder().decode(parts[4]);
                patients.put(fullName, new Patient(fullName, phoneNumber, personAge, personGender, passwordHash));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDoctors() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DOCTORS_FILE))) {
            for (Doctor doctor : doctors.values()) {
                writer.println(doctor.phoneNumber + "," + doctor.fullName + "," + doctor.personAge + "," + doctor.personGender + "," + Hash.encode(doctor.getPasswordHash()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePatients() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
            for (Patient patient : patients.values()) {
                writer.println(patient.phoneNumber + "," + patient.fullName + "," + patient.personAge + "," + patient.personGender + "," + Hash.encode(patient.getPasswordHash()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void registerDoctor(String fullName, String phoneNumber, int personAge, String personGender, String password) {
        Doctor doctor = new Doctor(fullName, phoneNumber, personAge, personGender, Hash.getHash(password));
        doctors.put(fullName, doctor);
        System.out.println("Doctor registered successfully.");
        saveDoctors();
    }

    void registerPatient(String fullName, String phoneNumber, int personAge, String personGender, String password) {
        Patient patient = new Patient(fullName, phoneNumber, personAge, personGender, Hash.getHash(password));
        patients.put(fullName, patient);
        System.out.println("Patient registered successfully.");
        savePatients();
    }

    Doctor getDoctor(String fullName) {
        return doctors.get(fullName);
    }

    Patient getPatient(String fullName) {
        return patients.get(fullName);
    }

    Person login(String fullName, String password, String userType) {
        if (loginAttempts.containsKey(fullName) && loginAttempts.get(fullName) >= MAX_LOGIN_ATTEMPTS) {
            System.out.println("Login attempts exceeded. Please contact support.");
            return null;
        }

        // Check if the user type is Doctor
        if (userType.equals("Doctor")) {
            Doctor doctor = doctors.get(fullName);
            if (doctor != null && doctor.authenticate(password)) {
                loginAttempts.remove(fullName); // Reset login attempts on successful login
                return doctor;
            }
        }
        // Check if the user type is Patient
        else if (userType.equals("Patient")) {
            Patient patient = patients.get(fullName);
            if (patient != null && patient.authenticate(password)) {
                loginAttempts.remove(fullName); // Reset login attempts on successful login
                return patient;
            }
        }

        // Increment login attempts for unsuccessful logins
        int attempts = loginAttempts.getOrDefault(fullName, 0) + 1;
        loginAttempts.put(fullName, attempts);

        System.out.println("Invalid credentials. Login failed. Attempts left: " + (MAX_LOGIN_ATTEMPTS - attempts));

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            System.out.println("Login attempts exceeded. Please contact support.");
        }

        return null;
    }
}

class Hash {
    public static byte[] getHash(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("The algorithm does not exist");
            return null;
        }
    }

    public static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}

public class health {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HealthSystem healthSystem = new HealthSystem();
        Person currentUser = null;

        while (true) {
            System.out.println("Health System Management");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            int mainChoice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            switch (mainChoice) {
                case 1:
                    System.out.println("Registration:");
                    System.out.println("1. Register Doctor");
                    System.out.println("2. Register Patient");
                    System.out.print("Select an option: ");
                    int registrationChoice = scanner.nextInt();
                    scanner.nextLine(); // consume the newline character

                    System.out.print("Enter full name: ");
                    String fullName = scanner.nextLine();
                    System.out.print("Enter phone number: ");
                    String phoneNumber = scanner.nextLine();
                    System.out.print("Enter age: ");
                    int personAge = scanner.nextInt();
                    scanner.nextLine(); // consume the newline character
                    System.out.print("Enter gender: ");
                    String personGender = scanner.nextLine();
                    System.out.print("Set password: ");
                    String password = scanner.nextLine();

                    if (registrationChoice == 1) {
                        healthSystem.registerDoctor(fullName, phoneNumber, personAge, personGender, password);
                    } else if (registrationChoice == 2) {
                        healthSystem.registerPatient(fullName, phoneNumber, personAge, personGender, password);
                    } else {
                        System.out.println("Invalid option.");
                    }
                    break;

                case 2:
                    System.out.println("Login:");
                    System.out.print("Enter full name: ");
                    String loginFullName = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String loginPassword = scanner.nextLine();
                    System.out.print("Enter user type (Doctor/Patient): ");
                    String userType = scanner.nextLine();

                    currentUser = healthSystem.login(loginFullName, loginPassword, userType);
                    if (currentUser != null) {
                        System.out.println("Login Successful.");
                        currentUser.displayDetails();
                        // Navigate to Doctor or Patient section
                        if (currentUser instanceof Doctor) {
                            doctorSection(scanner, (Doctor) currentUser, healthSystem);
                        } else if (currentUser instanceof Patient) {
                        	patientSection(scanner, (Patient) currentUser, healthSystem);
                        }
                    } else {
                        System.out.println("Invalid credentials. Login failed.");
                    }
                    break;

                case 3:
                    System.out.println("Exiting the application. Goodbye!");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void doctorSection(Scanner scanner, Doctor currentDoctor, HealthSystem healthSystem) {
        while (true) {
            System.out.println("Doctor Actions:");
            System.out.println("1. Enter Medical Information");
            System.out.println("2. View Information");
            System.out.println("3. Logout");
            System.out.print("Select an option: ");

            int doctorChoice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            switch (doctorChoice) {
                case 1:
                    System.out.print("Enter patient full name: ");
                    String patientFullName = scanner.nextLine();
                    Patient patientForInfo = healthSystem.getPatient(patientFullName);

                    if (patientForInfo != null) {
                        patientForInfo.displayDetails();
                        System.out.print("Enter medical condition: ");
                        String medicalCondition = scanner.nextLine();
                        System.out.print("Enter medical treatment: ");
                        String medicalTreatment = scanner.nextLine();
                        currentDoctor.recordMedicalInformation(patientForInfo, medicalCondition, medicalTreatment);
                    } else {
                        System.out.println("Patient not found.");
                    }
                    break;

                case 2:
                    currentDoctor.displayDetails();
                    break;

                case 3:
                    return;  // Return to the main page after logout

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void patientSection(Scanner scanner, Patient currentPatient, HealthSystem healthSystem) {
        while (true) {
            System.out.println("Patient Actions:");
            System.out.println("1. View Information");
            System.out.println("2. View Medical History");
            System.out.println("3. Logout");
            System.out.print("Select an option: ");

            int patientChoice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            switch (patientChoice) {
                case 1:
                    currentPatient.displayDetails();
                    break;
                case 2:
                    currentPatient.viewMedicalHistory();
                    break;

                case 3:
                    currentPatient = null;
                    return;  // Return to the main page after logout

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
