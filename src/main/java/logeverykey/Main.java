package logeverykey;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Main extends JFrame implements NativeKeyListener {

	private static final long serialVersionUID = 1L;

	private Config config;

	private JTextField logPath;

	private Logger logger;

	public Main() throws IOException {
		super("LogEveryKey");
		setSize(640, 480);

		registerNativeHook();

		config = new Config();
		logger = new Logger(config.getLogPath().toFile());

		JPanel panel = createStack();
		panel.add(new JLabel("Log"));
		panel.add(log());
		panel.add(open(), "wrap");
		add(panel);

		createTrayIcon();
		pack();
	}

	private void createTrayIcon() {
		PopupMenu popup = new PopupMenu();
		MenuItem quit = new MenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					System.exit(1);
				}
			}
		});
		popup.add(quit);

		SystemTray tray = SystemTray.getSystemTray();
		TrayIcon trayIcon = new TrayIcon(createImage("/img/keyboard.gif", "tray icon"));
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.this.setVisible(true);
			}
		});
		trayIcon.setPopupMenu(popup);
		try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	protected static Image createImage(String path, String description) {
		URL imageURL = Main.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

	private void registerNativeHook() {
		GlobalScreen.addNativeKeyListener(this);
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);
		GlobalScreen.setEventDispatcher(new SwingDispatchService());
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();

			System.exit(1);
		}
	}

	private Component open() {
		JButton btn = new JButton("Open...");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(config.getLogPath().toFile());
				} catch (IOException e1) {
					error(e1);
				} catch (IllegalArgumentException e2) {
					error(e2);
				}
			}
		});
		return btn;
	}

	private Component log() {
		logPath = new JTextField(config.getLogPath().toString(), 40);
		logPath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setLogPath();
			}
		});
		return logPath;
	}

	private JPanel createStack() {
		return new JPanel(new MigLayout("fillx"));
	}

	private void setLogPath() {
		JFileChooser fc = new JFileChooser(config.getLogPath().toString());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int response = fc.showOpenDialog(Main.this);
		if (response == JFileChooser.APPROVE_OPTION) {
			try {
				config.setLogPath(fc.getSelectedFile().toString());
			} catch (BackingStoreException e) {
				error(e);
			}
		}
		updateLogPath();
	}

	private void error(Exception e) {
		JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void updateLogPath() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				logPath.setText(config.getLogPath().toString());
			}
		});
	}

	public void nativeKeyPressed(NativeKeyEvent e) {}

	public void nativeKeyReleased(NativeKeyEvent e) {
		try {
			if (NativeKeyEvent.VC_ENTER == e.getKeyCode())
				logger.append("\n");
			else
				logger.append(NativeKeyEvent.getKeyText(e.getKeyCode()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void nativeKeyTyped(NativeKeyEvent e) {}

	protected void close() throws IOException {
		logger.close();
	}

	public static void main(String[] args) throws NativeHookException, IOException {
		final Main main = new Main();
		main.setVisible(true);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run() {
				try {
					main.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}));
	}

}
