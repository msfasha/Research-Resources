package TestBed;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;


public class TestCaret
{
    public TestCaret()
    {
        JLabel label = new JLabel("Move the caret...");
        JTextArea area = new JTextArea();
        area.addCaretListener(new MyCaretListener(label));

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setBounds(new Rectangle(400, 400, 400, 400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(area, BorderLayout.CENTER);
        frame.add(label, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static class MyCaretListener implements CaretListener
    {
        private final JLabel label;

        MyCaretListener(JLabel label)
        {
            this.label = label;
        }

        @Override
        public void caretUpdate(CaretEvent e)
        {
            JTextComponent textComp = (JTextComponent) e.getSource();
            try
            {
                Rectangle rect = textComp.getUI().modelToView(textComp, e.getDot());
                label.setText(rect.toString());
            }
            catch (BadLocationException ex)
            {
                throw new RuntimeException("Failed to get pixel position of caret", ex);
            }
        }   
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new TestCaret();
            }
        });
    }
}