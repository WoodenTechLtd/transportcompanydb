package com.transportSys.gui;



import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;


// importing tools for auto-detect screen height
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import com.transportSys.payments.paymentsWindow;  // payments window
import com.transportSys.productlines.ProductLinesWindow; // product lines
import com.transportSys.employees.employeesWindow;
import javax.swing.JLabel; // employee window





public class appDashboard extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					appDashboard frame = new appDashboard();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public appDashboard() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);  // Opens application in full screen
		setBounds(100, 100, 1060, 750);	// changes to this resolution in windowed mode
		contentPanel = new JPanel();
		contentPanel.setBackground(new Color(255, 255, 255));

		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		//----------- Changing the application icon -----------
        ImageIcon icon = new ImageIcon("images/applicationIcon.png");
        setIconImage(icon.getImage());
	    
	    //-----------------------------------------------------------
	    
		JPanel panelMenu = new JPanel();
		panelMenu.setBackground(new Color(30, 144, 255));
		contentPanel.add(panelMenu);
		panelMenu.setLayout(null);
		
		JPanel panelLogo = new JPanel();
		panelLogo.setBounds(10, 10, 310, 190);
		panelMenu.add(panelLogo);
		panelLogo.setLayout(null);

		// Load the image
		ImageIcon originalIcon = new ImageIcon("images/logo.jpg");

		// Scale the image to fit inside the panelLogo
		Image scaledImage = originalIcon.getImage().getScaledInstance(panelLogo.getWidth(), panelLogo.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon scaledIcon = new ImageIcon(scaledImage);

		JLabel labelLogo = new JLabel();
		labelLogo.setIcon(scaledIcon);
		labelLogo.setBounds(0, 0, panelLogo.getWidth(), panelLogo.getHeight()); 
		panelLogo.add(labelLogo);


		
		JButton btnCustomers = new JButton("Customers");
		btnCustomers.setForeground(new Color(255, 255, 255));
		btnCustomers.setFont(new Font("DialogInput", Font.BOLD, 35));
		btnCustomers.setBackground(new Color(0, 0, 139));
		btnCustomers.setBounds(10, 240, 310, 51);
		panelMenu.add(btnCustomers);
		
		JButton btnOrders = new JButton("Orders");
		btnOrders.setForeground(new Color(255, 255, 255));
		btnOrders.setFont(new Font("DialogInput", Font.BOLD, 35));
		btnOrders.setBackground(new Color(0, 0, 139));
		btnOrders.setBounds(10, 301, 310, 51);
		panelMenu.add(btnOrders);
		
		
		JButton btnPayments = new JButton("Payments");
		btnPayments.setForeground(new Color(255, 255, 255));
		btnPayments.setFont(new Font("DialogInput", Font.BOLD, 35));
		btnPayments.setBackground(new Color(0, 0, 139));
		btnPayments.setBounds(10, 362, 310, 51);
		btnPayments.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        // Open the paymentsWindow when the "Payments" button is clicked
		        new paymentsWindow();
		    }
		});
		panelMenu.add(btnPayments);


		
		JButton btnDeliveries = new JButton("Deliveries");
		btnDeliveries.setForeground(new Color(255, 255, 255));
		btnDeliveries.setFont(new Font("DialogInput", Font.BOLD, 35));
		btnDeliveries.setBackground(new Color(0, 0, 139));
		btnDeliveries.setBounds(10, 423, 310, 51);
		panelMenu.add(btnDeliveries);
		
		JButton btnProductLines = new JButton("Product Lines");
		btnProductLines.setForeground(new Color(255, 255, 255));
		btnProductLines.setFont(new Font("DialogInput", Font.BOLD, 35));
		btnProductLines.setBackground(new Color(0, 0, 139));
		btnProductLines.setBounds(10, 484, 310, 51);
		btnProductLines.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        System.out.println("Button Clicked!"); // Check if this is printed
		        new ProductLinesWindow();
		    }
		});

		panelMenu.add(btnProductLines);
		
		JButton btnEmployees = new JButton("Employees");
		btnEmployees.setForeground(new Color(255, 255, 255));
		btnEmployees.setFont(new Font("DialogInput", Font.BOLD, 35));
		btnEmployees.setBackground(new Color(0, 0, 139));
		btnEmployees.setBounds(10, 545, 310, 51);
		btnEmployees.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        new employeesWindow();
		    }
		});
		panelMenu.add(btnEmployees);
		
		JButton btnOffice = new JButton("Offices");
		btnOffice.setForeground(new Color(255, 255, 255));
		btnOffice.setFont(new Font("DialogInput", Font.BOLD, 35));
		btnOffice.setBackground(new Color(0, 0, 139));
		btnOffice.setBounds(10, 606, 310, 51);
		panelMenu.add(btnOffice);
		
		
		// ----- Combined Chart -----
		
		JPanel panelCombinedChart = new JPanel();
		panelCombinedChart.setBounds(358, 424, 500, 275);
		contentPanel.add(panelCombinedChart);
		panelCombinedChart.setLayout(null);

		JLabel lblCombinedChart = new JLabel();
		lblCombinedChart.setBounds(10, 10, 480, 255);
		panelCombinedChart.add(lblCombinedChart);

		// Load the image
		ImageIcon combinedChartIcon = new ImageIcon("images/combinedCharts.png");

		// Scale the image to fit inside the lblCombinedChart
		Image scaledChartImage = combinedChartIcon.getImage().getScaledInstance(lblCombinedChart.getWidth(), lblCombinedChart.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon scaledChartIcon = new ImageIcon(scaledChartImage);

		// Set the scaled image to the lblCombinedChart
		lblCombinedChart.setIcon(scaledChartIcon);
		
		
		// ----- Circle Chart -----
		JPanel panelCircleChart = new JPanel();
		panelCircleChart.setLayout(null);
		panelCircleChart.setBounds(358, 10, 544, 379);
		contentPanel.add(panelCircleChart);

		JLabel lblCircleChart = new JLabel();
		lblCircleChart.setBounds(10, 10, 524, 359);
		panelCircleChart.add(lblCircleChart);

		// Load the image for monthly-salesCircleChart
		ImageIcon circleChartIcon = new ImageIcon("images/monthly-salesCircleChart.png");

		// Scale the image to fit inside the lblCircleChart
		Image scaledCircleChartImage = circleChartIcon.getImage().getScaledInstance(lblCircleChart.getWidth(), lblCircleChart.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon scaledCircleChartIcon = new ImageIcon(scaledCircleChartImage);

		// Set the scaled image to the lblCircleChart
		lblCircleChart.setIcon(scaledCircleChartIcon);

		// ----- Auto detect window height, and keep the length of panelMenu at full height -----
		contentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panelMenu.setBounds(0, 0, 330, contentPanel.getHeight());
            }
        });
		
		// Prevent any button from being automatically focused when the frame is opened
		getRootPane().setDefaultButton(null);

	}
}