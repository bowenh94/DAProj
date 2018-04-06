package Client;

import java.awt.EventQueue;
import javax.swing.JFrame;


public class DAClient extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DAClient() {

        add(new Board());
        
        setResizable(false);
        pack();
        
        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                JFrame ex = new DAClient();
                ex.setVisible(true);                
            }
        });
    }
}
