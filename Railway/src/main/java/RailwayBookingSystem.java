
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class RailwayBookingSystem extends JFrame {

    private static final String[] STATIONS = {
        "Mumbai Central", "New Delhi", "Howrah", "Chennai Central", "Bangalore City", "Hyderabad", 
        "Ahmedabad", "Pune", "Lucknow", "Jaipur", "Patna", "Bhopal", "Udupi", "Nagpur", "Kanpur Central", 
        "Coimbatore", "Thiruvananthapuram", "Visakhapatnam", "Vijayawada", "Madurai", "Surat", 
        "Varanasi", "Amritsar", "Jammu Tawi", "Guwahati", "Dehradun", "Allahabad", "Ranchi", "Raipur",
        "Jodhpur", "Udaipur", "Durgapur", "Siliguri", "Dhanbad", "Gwalior", "Agra Cantt", "Indore",
        "Trichy", "Kozhikode", "Cuttack", "Bhubaneswar", "Salem", "Tirunelveli", "Erode", "Hubli", 
        "Belgaum", "Mysore", "Goa (Madgaon)", "Kharagpur", "Jabalpur", "Bareilly", "Aligarh", "Asansol", 
        "Gaya", "Katni", "Bhagalpur", "Saharanpur", "Ambala", "Panipat", "Kota", "Ajmer", "Mathura",
        "Meerut", "Alwar", "Sonepur", "Purnia", "Gorakhpur", "Muzaffarpur", "Darbhanga", "Begusarai", 
        "Sambalpur", "Berhampur", "Tatanagar", "Bilaspur", "Anand", "Vadodara", "Surendra Nagar", 
        "Bhavnagar", "Porbandar", "Rajkot", "Junagadh", "Jamnagar", "Morbi", "Veraval", "Okha", "Nanded", 
        "Aurangabad", "Kolhapur", "Shirdi", "Nashik", "Manmad", "Wardha", "Chandrapur", "Solapur", "Karwar",
        "Palakkad", "Thrissur", "Kollam", "Nagercoil", "Kanyakumari", "Alappuzha", "Kannur", "Shoranur",
        "Mangalore", "Puducherry", "Vellore", "Kumbakonam", "Tuticorin", "Tirupur", "Ratlam", "Rewa", 
        "Satna", "Itarsi", "Sagar", "Chhindwara", "Hoshangabad", "Sehore", "Chhatarpur", "Damoh", "Tikamgarh", 
        "Datia", "Narsinghpur", "Betul", "Adilabad", "Karimnagar", "Khammam", "Nizamabad", "Warangal", 
        "Nalgonda", "Mahbubnagar", "Vikarabad", "Guntur", "Ongole", "Nellore", "Anantapur", "Kadapa", 
        "Kurnool", "Srikakulam", "Eluru", "Rajahmundry", "Tuni", "Chilakaluripet", "Tenali", "Peddapalli", 
        "Karur", "Namakkal", "Perambalur", "Dindigul", "Virudhunagar", "Sivakasi", "Rajapalayam", 
        "Sattur", "Chennai Egmore", "Tambaram", "Avadi", "Tiruvallur", "Arakkonam", "Katpadi", "Tirupati", 
        "Renigunta", "Gudur", "Krishna Canal", "Chirala", "Vetapalem", "Singarayakonda", "Kavali", 
        "Bitragunta", "Nellore South", "Tada", "Sullurupeta", "Sri City", "Jolarpettai", "Coimbatore North", 
        "Podanur", "Palghat Town", "Shoranur", "Trissur", "Ernakulam", "Kollam", "Trivandrum Central"
    };

    // Constants for seat availability
    private static final int TOTAL_CONFIRMED_SEATS = 60;
    private static final int TOTAL_RAC_SEATS = 10;
    private static final int TOTAL_WAITING_LIST = 30;
    
    // Track available seats
    private int confirmedSeatsAvailable = TOTAL_CONFIRMED_SEATS;
    private int racSeatsAvailable = TOTAL_RAC_SEATS;
    private int waitingListAvailable = TOTAL_WAITING_LIST;
    
    // Queues for managing RAC and Waiting List
    private Queue<Ticket> racQueue = new LinkedList<>();
    private Queue<Ticket> waitingQueue = new LinkedList<>();

    class Ticket {
        String pnr, from, to, trainName, trainNo, date, coachType, seatNo, status, type, berth;
        double fare;
        int waitingNumber;

        Ticket(String from, String to, String trainNo, String trainName, String date, String coachType, boolean isTatkal) {
            this.from = from;
            this.to = to;
            this.trainNo = trainNo;
            this.trainName = trainName;
            this.date = date;
            this.coachType = coachType;
            this.type = isTatkal ? "Tatkal" : "Regular";
            this.pnr = generatePNR();
            this.status = assignStatus();
            this.seatNo = generateSeat(coachType);
            this.berth = assignBerth();
            this.fare = calculateFare(coachType, isTatkal);
        }

        private String generatePNR() {
            Random rand = new Random();
            long pnrNum = 1000000000L + (long)(rand.nextDouble() * 8999999999L);
            return String.valueOf(pnrNum);
        }

        private String assignStatus() {
            if (confirmedSeatsAvailable > 0) {
                confirmedSeatsAvailable--;
                return "Confirmed";
            } else if (racSeatsAvailable > 0) {
                racSeatsAvailable--;
                racQueue.add(this);
                return "RAC-" + (TOTAL_RAC_SEATS - racSeatsAvailable);
            } else if (waitingListAvailable > 0) {
                waitingListAvailable--;
                waitingNumber = TOTAL_WAITING_LIST - waitingListAvailable;
                waitingQueue.add(this);
                return "WL-" + waitingNumber;
            }
            return "Not Available";
        }

        private String assignBerth() {
            if (status.startsWith("WL")) {
                return "Not Allocated";
            }
            
            if (status.startsWith("RAC")) {
                return "Side Lower";
            }
            
            Random rand = new Random();
            String[] berths = {"Lower", "Middle", "Upper", "Side Lower", "Side Upper"};
            
            // For 1AC, we have only Lower and Upper berths
            if (coachType.equals("1AC")) {
                String[] ac1Berths = {"Lower", "Upper"};
                return ac1Berths[rand.nextInt(ac1Berths.length)];
            }
            
            return berths[rand.nextInt(berths.length)];
        }

        private String generateSeat(String coachType) {
            if (status.startsWith("WL")) {
                return "Not Allocated";
            }
            
            Random rand = new Random();
            String coach;
            int seat;
            
            if (status.startsWith("RAC")) {
                seat = rand.nextInt(10) + 1; // RAC seats are side lower berths
                return "RAC-" + seat;
            }
            
            seat = rand.nextInt(72) + 1; // Seat number between 1-72
            if (coachType.equals("SL")) {
                coach = "S" + (rand.nextInt(12) + 1); // Sleeper coach
            } else if (coachType.equals("2A")) {
                coach = "A" + (rand.nextInt(4) + 1); // 2nd AC coach
            } else if (coachType.equals("3A")) {
                coach = "B" + (rand.nextInt(4) + 1); // 3rd AC coach
            } else { // 1AC
                coach = "H" + (rand.nextInt(2) + 1); // 1AC coach (H for First AC)
            }
            return coach + "-" + seat;
        }

        private double calculateFare(String coachType, boolean isTatkal) {
            double baseFare = 500;
            
            if (coachType.equals("3A")) baseFare = 1000;
            else if (coachType.equals("2A")) baseFare = 1500;
            else if (coachType.equals("1AC")) baseFare = 2500;  // 1AC fare
            else baseFare = 700;
            
            // Apply discount for waiting list
            if (status.startsWith("WL")) {
                baseFare *= 0.8; // 20% discount for waiting list
            }
            
            return isTatkal ? baseFare * 1.5 : baseFare;
        }

        @Override
        public String toString() {
            return String.format(
                "PNR: %s\nTrain No: %s\nTrain Name: %s\nFrom: %s\nTo: %s\nJourney Date: %s\n" +
                "Ticket Type: %s\nCoach Type: %s\nSeat No: %s\nBerth: %s\nStatus: %s\nFare: ₹%.2f\n\n",
                pnr, trainNo, trainName, from, to, date, type, coachType, seatNo, berth, status, fare
            );
        }
    }

    private final ArrayList<Ticket> tickets = new ArrayList<>();
    private final JTextArea ticketOutput = new JTextArea();
    private final JLabel statusLabel = new JLabel("Available: Confirmed(" + TOTAL_CONFIRMED_SEATS + 
                                                "), RAC(" + TOTAL_RAC_SEATS + "), WL(" + TOTAL_WAITING_LIST + ")");

    public RailwayBookingSystem() {
        setTitle("Indian Railway Booking System");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel bookPanel = new JPanel(new GridLayout(10, 2, 10, 10));

        JComboBox<String> fromBox = new JComboBox<>(STATIONS);
        JComboBox<String> toBox = new JComboBox<>(STATIONS);
        JTextField trainNoField = new JTextField();
        JTextField trainNameField = new JTextField();
        JTextField dateField = new JTextField();
        String[] coaches = {"SL", "2A", "3A", "1AC"};
        JComboBox<String> coachBox = new JComboBox<>(coaches);
        JCheckBox tatkalCheck = new JCheckBox("Tatkal Ticket");
        JButton bookButton = new JButton("Book Ticket");
        JButton cancelButton = new JButton("Cancel Ticket (by PNR)");

        bookPanel.add(new JLabel("From Station:"));
        bookPanel.add(fromBox);
        bookPanel.add(new JLabel("To Station:"));
        bookPanel.add(toBox);
        bookPanel.add(new JLabel("Train No:"));
        bookPanel.add(trainNoField);
        bookPanel.add(new JLabel("Train Name:"));
        bookPanel.add(trainNameField);
        bookPanel.add(new JLabel("Journey Date (DD/MM/YYYY):"));
        bookPanel.add(dateField);
        bookPanel.add(new JLabel("Coach Type:"));
        bookPanel.add(coachBox);
        bookPanel.add(new JLabel(""));
        bookPanel.add(tatkalCheck);
        bookPanel.add(statusLabel);
        bookPanel.add(bookButton);
        bookPanel.add(new JLabel(""));
        bookPanel.add(cancelButton);

        JPanel displayPanel = new JPanel(new BorderLayout());
        ticketOutput.setEditable(false);
        JScrollPane scroll = new JScrollPane(ticketOutput);
        JButton refreshButton = new JButton("View All Tickets");
        JButton viewStatusButton = new JButton("View Current Status");

        displayPanel.add(scroll, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewStatusButton);
        displayPanel.add(buttonPanel, BorderLayout.SOUTH);

        bookButton.addActionListener(e -> {
            String from = (String) fromBox.getSelectedItem();
            String to = (String) toBox.getSelectedItem();
            String trainNo = trainNoField.getText().trim();
            String trainName = trainNameField.getText().trim();
            String date = dateField.getText().trim();
            String coachType = (String) coachBox.getSelectedItem();
            boolean isTatkal = tatkalCheck.isSelected();

            if (from.equals(to)) {
                JOptionPane.showMessageDialog(this, "From and To stations cannot be the same!");
                return;
            }
            if (trainNo.isEmpty() || trainName.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Train No, Train Name, and Date are mandatory!");
                return;
            }

            if (confirmedSeatsAvailable <= 0 && racSeatsAvailable <= 0 && waitingListAvailable <= 0) {
                JOptionPane.showMessageDialog(this, "No seats available in any category!");
                return;
            }

            Ticket ticket = new Ticket(from, to, trainNo, trainName, date, coachType, isTatkal);
            tickets.add(ticket);
            
            // Update status label
            statusLabel.setText("Available: Confirmed(" + confirmedSeatsAvailable + 
                              "), RAC(" + racSeatsAvailable + "), WL(" + waitingListAvailable + ")");
            
            JOptionPane.showMessageDialog(this, "Ticket Booked Successfully!\nPNR: " + ticket.pnr + 
                                        "\nStatus: " + ticket.status + "\nBerth: " + ticket.berth);
        });

        refreshButton.addActionListener(e -> {
            ticketOutput.setText("");
            if (tickets.isEmpty()) {
                ticketOutput.setText("No tickets booked yet.");
            } else {
                for (Ticket t : tickets) {
                    ticketOutput.append(t.toString());
                }
            }
        });

        viewStatusButton.addActionListener(e -> {
            statusLabel.setText("Available: Confirmed(" + confirmedSeatsAvailable + 
                              "), RAC(" + racSeatsAvailable + "), WL(" + waitingListAvailable + ")");
            JOptionPane.showMessageDialog(this, "Current Booking Status:\n" +
                "Confirmed Seats Available: " + confirmedSeatsAvailable + "\n" +
                "RAC Seats Available: " + racSeatsAvailable + "\n" +
                "Waiting List Available: " + waitingListAvailable);
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pnrToCancel = JOptionPane.showInputDialog(RailwayBookingSystem.this, "Enter PNR to cancel:");
                if (pnrToCancel == null || pnrToCancel.isEmpty()) return;
                boolean found = false;
                for (int i = 0; i < tickets.size(); i++) {
                    Ticket ticket = tickets.get(i);
                    if (ticket.pnr.equals(pnrToCancel)) {
                        // Handle cancellation based on status
                        if (ticket.status.startsWith("Confirmed")) {
                            confirmedSeatsAvailable++;
                            promoteFromRAC();
                        } else if (ticket.status.startsWith("RAC")) {
                            racSeatsAvailable++;
                            promoteFromWaitingList();
                        } else if (ticket.status.startsWith("WL")) {
                            waitingListAvailable++;
                        }   tickets.remove(i);
                        JOptionPane.showMessageDialog(RailwayBookingSystem.this, "Ticket with PNR " + pnrToCancel + " cancelled successfully.\n" +
                                "Status: " + ticket.status + "\n" +
                                        "Refund Amount: ₹" + (ticket.fare * (ticket.status.startsWith("WL") ? 0.8 : 1.0)));
                        found = true;
                        // Update status label
                        statusLabel.setText("Available: Confirmed(" + confirmedSeatsAvailable +
                                "), RAC(" + racSeatsAvailable + "), WL(" + waitingListAvailable + ")");
                        break;
                    }
                }
                if (!found) {
                    JOptionPane.showMessageDialog(RailwayBookingSystem.this, "PNR not found.");
                }
            }
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Book Ticket", bookPanel);
        tabs.add("View Tickets", displayPanel);
        add(tabs);
    }

    private void promoteFromRAC() {
        if (!racQueue.isEmpty() && confirmedSeatsAvailable > 0) {
            Ticket racTicket = racQueue.poll();
            // Update the ticket status
            for (Ticket t : tickets) {
                if (t.pnr.equals(racTicket.pnr)) {
                    t.status = "Confirmed";
                    t.berth = t.assignBerth();
                    t.seatNo = t.generateSeat(t.coachType);
                    confirmedSeatsAvailable--;
                    break;
                }
            }
            promoteFromWaitingList(); // Now we have a free RAC seat
        }
    }

    private void promoteFromWaitingList() {
        if (!waitingQueue.isEmpty() && racSeatsAvailable > 0) {
            Ticket wlTicket = waitingQueue.poll();
            // Update the ticket status
            for (Ticket t : tickets) {
                if (t.pnr.equals(wlTicket.pnr)) {
                    t.status = "RAC-" + (TOTAL_RAC_SEATS - racSeatsAvailable + 1);
                    t.berth = "Side Lower";
                    t.seatNo = t.generateSeat(t.coachType);
                    racSeatsAvailable--;
                    racQueue.add(t);
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RailwayBookingSystem().setVisible(true));
    }
}