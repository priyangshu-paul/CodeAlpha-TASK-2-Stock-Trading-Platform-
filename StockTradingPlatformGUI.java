import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.Timer;
import javax.swing.*;

// Stock class
class Stock {
    String symbol;
    double price;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    @Override
    public String toString() {
        return symbol + " - $" + price;
    }
}

// Transaction class
class Transaction {
    String type;
    String symbol;
    int quantity;
    double price;
    Date timestamp;

    public Transaction(String type, String symbol, int quantity, double price) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        return timestamp + " | " + type + " | " + symbol + " | Qty: " + quantity + " | Price: $" + price;
    }
}

// Portfolio class
class Portfolio {
    private java.util.Map<String, Integer> holdings = new HashMap<>();
    private java.util.List<Transaction> transactions = new ArrayList<>();

    public void buyStock(String symbol, int qty, double price) {
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + qty);
        transactions.add(new Transaction("BUY", symbol, qty, price));
    }

    public void sellStock(String symbol, int qty, double price) {
        if (holdings.getOrDefault(symbol, 0) >= qty) {
            holdings.put(symbol, holdings.get(symbol) - qty);
            transactions.add(new Transaction("SELL", symbol, qty, price));
        } else {
            JOptionPane.showMessageDialog(null, "Not enough stock to sell!");
        }
    }

    public String getPortfolioString() {
        if (holdings.isEmpty()) return "No holdings.";
        StringBuilder sb = new StringBuilder();
        holdings.forEach((symbol, qty) -> sb.append(symbol).append(": ").append(qty).append(" shares\n"));
        return sb.toString();
    }

    public String getTransactionHistoryString() {
        if (transactions.isEmpty()) return "No transactions.";
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : transactions) {
            sb.append(tx).append("\n");
        }
        return sb.toString();
    }

    public void savePortfolioToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Transaction tx : transactions) {
                writer.println(tx.timestamp + "|" + tx.type + "|" + tx.symbol + "|" + tx.quantity + "|" + tx.price);
            }
            JOptionPane.showMessageDialog(null, "Portfolio saved to " + filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving portfolio: " + e.getMessage());
        }
    }
}

// Market class
class Market {
    private java.util.Map<String, Stock> stocks = new HashMap<>();

    public Market() {
        initializeMarket();
    }

    private void initializeMarket() {
        String[] symbols = {
            "AAPL", "GOOGL", "AMZN", "MSFT", "TSLA", "FB", "NFLX", "NVDA", "INTC", "AMD",
            "BABA", "ORCL", "IBM", "ADBE", "SAP", "PYPL", "CRM", "UBER", "LYFT", "SQ",
            "TWTR", "SHOP", "ZM", "SPOT", "DOCU", "ATVI", "EA", "CSCO", "QCOM", "MU",
            "T", "VZ", "PEP", "KO", "MCD", "NKE", "DIS", "JNJ", "PFE", "MRK",
            "XOM", "CVX", "BA", "GE", "GM", "F", "WMT", "HD", "JPM", "BAC"
        };
        Random rand = new Random();

        for (String symbol : symbols) {
            double price = 50 + (3000 - 50) * rand.nextDouble();
            stocks.put(symbol, new Stock(symbol, Math.round(price * 100.0) / 100.0));
        }
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol.toUpperCase());
    }

    public String getMarketDataString() {
        StringBuilder sb = new StringBuilder();
        for (Stock stock : stocks.values()) {
            sb.append(stock).append("\n");
        }
        return sb.toString();
    }

    public String[] getAllSymbols() {
        return stocks.keySet().toArray(new String[0]);
    }
}

// GUI Trading Platform
public class StockTradingPlatformGUI extends JFrame {
    private Market market = new Market();
    private Portfolio portfolio = new Portfolio();
    private JTextArea displayArea = new JTextArea(15, 50);
    private final String graphImagePath = "graph.png"; // Replace with your image path
    private String[] stockSymbols;

    public StockTradingPlatformGUI() {
        setTitle("📈 Stock Trading Platform");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        stockSymbols = market.getAllSymbols();

        // Graph label
        JLabel graphLabel = new JLabel();
        graphLabel.setHorizontalAlignment(JLabel.CENTER);
        graphLabel.setIcon(new ImageIcon(graphImagePath));
        add(graphLabel, BorderLayout.NORTH);

        // Display area
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setBackground(Color.WHITE);
        displayArea.setForeground(Color.BLACK);
        displayArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton marketButton = createStyledButton("📊 Market Data");
        JButton buyButton = createStyledButton("🛒 Buy Stock");
        JButton sellButton = createStyledButton("💰 Sell Stock");
        JButton portfolioButton = createStyledButton("📂 Portfolio");
        JButton transactionButton = createStyledButton("📝 Transactions");
        JButton saveButton = createStyledButton("💾 Save Portfolio");

        buttonPanel.add(marketButton);
        buttonPanel.add(buyButton);
        buttonPanel.add(sellButton);
        buttonPanel.add(portfolioButton);
        buttonPanel.add(transactionButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        marketButton.addActionListener(e -> animateTextDisplay(market.getMarketDataString()));
        buyButton.addActionListener(e -> handleBuy());
        sellButton.addActionListener(e -> handleSell());
        portfolioButton.addActionListener(e -> animateTextDisplay(portfolio.getPortfolioString()));
        transactionButton.addActionListener(e -> animateTextDisplay(portfolio.getTransactionHistoryString()));
        saveButton.addActionListener(e -> portfolio.savePortfolioToFile("portfolio.txt"));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void animateTextDisplay(String content) {
        displayArea.setText("");
        Timer timer = new Timer();
        char[] chars = content.toCharArray();
        final int[] index = {0};

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (index[0] < chars.length) {
                    displayArea.append(String.valueOf(chars[index[0]]));
                    index[0]++;
                } else {
                    timer.cancel();
                }
            }
        }, 0, 10);
    }

    private void handleBuy() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBackground(Color.WHITE);

        JLabel symbolLabel = new JLabel("Select Stock Symbol to Buy:");
        symbolLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JComboBox<String> symbolDropdown = new JComboBox<>(stockSymbols);
        symbolDropdown.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel qtyLabel = new JLabel("Enter Quantity:");
        qtyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField qtyField = new JTextField();

        panel.add(symbolLabel);
        panel.add(symbolDropdown);
        panel.add(qtyLabel);
        panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Buy Stock", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String symbol = ((String) symbolDropdown.getSelectedItem()).toUpperCase();
            String qtyStr = qtyField.getText();

            Stock stock = market.getStock(symbol);
            if (stock == null) {
                JOptionPane.showMessageDialog(this, "Stock not found.");
                return;
            }

            try {
                int qty = Integer.parseInt(qtyStr);
                portfolio.buyStock(symbol, qty, stock.price);
                JOptionPane.showMessageDialog(this, "Bought " + qty + " shares of " + symbol);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.");
            }
        }
    }

    private void handleSell() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBackground(Color.WHITE);

        JLabel symbolLabel = new JLabel("Select Stock Symbol to Sell:");
        symbolLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JComboBox<String> symbolDropdown = new JComboBox<>(stockSymbols);
        symbolDropdown.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel qtyLabel = new JLabel("Enter Quantity:");
        qtyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField qtyField = new JTextField();

        panel.add(symbolLabel);
        panel.add(symbolDropdown);
        panel.add(qtyLabel);
        panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sell Stock", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String symbol = ((String) symbolDropdown.getSelectedItem()).toUpperCase();
            String qtyStr = qtyField.getText();

            Stock stock = market.getStock(symbol);
            if (stock == null) {
                JOptionPane.showMessageDialog(this, "Stock not found.");
                return;
            }

            try {
                int qty = Integer.parseInt(qtyStr);
                portfolio.sellStock(symbol, qty, stock.price);
                JOptionPane.showMessageDialog(this, "Sold " + qty + " shares of " + symbol);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockTradingPlatformGUI().setVisible(true));
    }
}
