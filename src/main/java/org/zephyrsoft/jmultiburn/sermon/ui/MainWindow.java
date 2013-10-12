package org.zephyrsoft.jmultiburn.sermon.ui;

import static org.zephyrsoft.jmultiburn.sermon.Setting.BASE_DIR;
import static org.zephyrsoft.jmultiburn.sermon.Setting.BURNERS;
import static org.zephyrsoft.jmultiburn.sermon.Setting.FONT_SIZE;
import static org.zephyrsoft.jmultiburn.sermon.Setting.TEMP_DIR;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zephyrsoft.jmultiburn.sermon.PropertyHolder;
import org.zephyrsoft.jmultiburn.sermon.SermonProvider;
import org.zephyrsoft.jmultiburn.sermon.model.Sermon;
import org.zephyrsoft.jmultiburn.sermon.model.SermonPart;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = -4438912327573399733L;
	
	private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);
	
	public static int CD_LENGTH = 78;
	
	private static String SEPARATOR = "|";
	
	@Autowired
	private PropertyHolder propertyHolder;
	
	@Autowired
	private SermonProvider sermonProvider;
	
	private final Object LOCK = new Object();
	
	private JPanel liste = null;
	private List<JButton> buttons = null;
	private String currentlyActiveButtonName = null;
	
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	private BurnWindow burnWindow = null;
	
	private int fontSize = 16;
	
	private GridBagConstraints dateConstraints;
	private GridBagConstraints nameConstraints;
	private GridBagConstraints buttonsConstraints;
	private GridBagConstraints backgroundConstraints;
	
	public MainWindow() {
		super("Predigten brennen");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		liste = new JPanel(new GridBagLayout());
		buttons = new LinkedList<JButton>();
		
		JScrollPane scroller = new JScrollPane(liste);
		scroller.getVerticalScrollBar().setUnitIncrement(20);
		setContentPane(scroller);
	}
	
	public void init() {
		try {
			fontSize = Integer.parseInt(propertyHolder.getProperty(FONT_SIZE));
		} catch (NumberFormatException e) {
			LOG.warn("could not parse \"{}\" to a number, using default font size {}",
				propertyHolder.getProperty(FONT_SIZE), fontSize);
		}
		
		dateConstraints = new GridBagConstraints();
		dateConstraints.gridx = 0;
		dateConstraints.fill = GridBagConstraints.BOTH;
		
		nameConstraints = new GridBagConstraints();
		nameConstraints.gridx = 1;
		nameConstraints.fill = GridBagConstraints.BOTH;
		
		buttonsConstraints = new GridBagConstraints();
		buttonsConstraints.gridx = 2;
		buttonsConstraints.fill = GridBagConstraints.BOTH;
		
		setSize(new Dimension(924, 668));
		setLocation(new Point(50, 50));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setVisible(true);
		
		// start refreshing periodically
		executor.scheduleAtFixedRate(createRunnable(), 0, 60, TimeUnit.SECONDS);
	}
	
	private Runnable createRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				readSermons();
			}
		};
	}
	
	public void readSermons() {
		synchronized (LOCK) {
			liste.removeAll();
			buttons.clear();
			
			List<Sermon> sermons = sermonProvider.readSermons();
			
			if (sermons == null || sermons.size() == 0) {
				liste.add(new JLabel("Keine MP3s zum Brennen vorhanden!"));
			} else {
				int rowNumber = 0;
				Font fontNormal = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
				Font fontBold = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
				for (Sermon sermon : sermons) {
					JLabel dateLabel = new JLabel(sermon.getDate());
					dateLabel.setFont(fontBold);
					dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
					JPanel datePanel = new JPanel(new BorderLayout());
					datePanel.add(dateLabel, BorderLayout.LINE_START);
					JLabel nameLabel = new JLabel(sermon.getName());
					nameLabel.setFont(fontBold);
					nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
					JPanel namePanel = new JPanel(new BorderLayout());
					namePanel.add(nameLabel, BorderLayout.LINE_START);
					
					List<JButton> createdButtons = new ArrayList<JButton>();
					for (SermonPart part : sermon) {
						final JButton button;
						if (sermon.getPartCount() == 1) {
							button = new JButton("Brennen");
						} else {
							button = new JButton("CD " + part.getIndex() + " brennen");
						}
						button.setName(sermon.getName() + "-" + part.getIndex());
						final SermonPart selectedSermonPart = part;
						button.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								currentlyActiveButtonName = button.getName();
								startBurning(selectedSermonPart);
							}
						});
						button.setForeground(Color.BLACK);
						button.setFont(fontNormal);
						buttons.add(button);
						createdButtons.add(button);
					}
					
					// add elements
					liste.add(datePanel, dateConstraints);
					liste.add(namePanel, nameConstraints);
					JPanel buttonPanel = new JPanel();
					for (JButton button : createdButtons) {
						buttonPanel.add(button);
					}
					liste.add(buttonPanel, buttonsConstraints);
					
					if (rowNumber % 2 == 1) {
						datePanel.setBackground(Color.lightGray);
						datePanel.setOpaque(true);
						namePanel.setBackground(Color.lightGray);
						namePanel.setOpaque(true);
						buttonPanel.setBackground(Color.lightGray);
						buttonPanel.setOpaque(true);
					}
					rowNumber++;
				}
			}
			
			handleButtonState();
			revalidate();
			liste.scrollRectToVisible(new Rectangle(0, ((int) liste.getSize().getHeight()) - 1, 1, 1));
		}
	}
	
	protected void exit() {
		// make sure it's safe to exit
		if (burnWindow == null) {
			System.exit(0);
		}
	}
	
	private void startBurning(SermonPart sermonPart) {
		handleButtonState();
		
		// open burn window
		List<String> burners = propertyHolder.getPropertyList(BURNERS);
		burnWindow =
			new BurnWindow(sermonPart, burners, propertyHolder.getProperty(BASE_DIR),
				propertyHolder.getProperty(TEMP_DIR), this);
	}
	
	public void closeBurnWindow() {
		currentlyActiveButtonName = null;
		handleButtonState();
		
		burnWindow.setVisible(false);
		burnWindow.dispose();
		burnWindow = null;
	}
	
	private void handleButtonState() {
		synchronized (LOCK) {
			boolean targetEnabledState = (currentlyActiveButtonName == null);
			
			for (JButton button : buttons) {
				if (button.getName().equals(currentlyActiveButtonName)) {
					button.setBackground(Color.GREEN);
				} else {
					button.setBackground(Color.WHITE);
				}
				button.setEnabled(targetEnabledState);
			}
		}
	}
	
}
