package de.uni_passau.fim.se2.sbse.neat.environments.visualisation;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.Agent;
import de.uni_passau.fim.se2.sbse.neat.environments.SinglePoleBalancing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Visualises the interaction of an agent with the Single Pole Balancing environment.
 */
public class SinglePoleVisualisation extends JFrame {
    private final SinglePoleBalancing environment;
    private final Agent agent;
    private final CartPanel cartPanel;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 400;
    private static final int REFRESH_RATE = 1000 / 120; // 60 FPS
    private static final int MAX_DURATION = 30000; // 30 seconds

    private final Timer timer;
    private final CountDownLatch latch;

    public SinglePoleVisualisation(SinglePoleBalancing environment, Agent agent, CountDownLatch latch) {
        this.environment = environment;
        this.agent = agent;
        this.latch = latch;

        setTitle("Single Pole Balancing Visualisation");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cartPanel = new CartPanel();
        add(cartPanel);
        this.setVisible(true);


        timer = new Timer(REFRESH_RATE, _ -> {
            if (!environment.isDone()) {
                step();
                cartPanel.repaint();
            } else {
                close();
            }
        });

        // Close the window after 30 seconds
        Timer durationTimer = new Timer(MAX_DURATION, _ -> close());
        durationTimer.setRepeats(false);

        timer.start();
        durationTimer.start();
    }

    private void close() {
        timer.stop();
        dispose();
        latch.countDown();
    }

    private void step() {
        List<Double> state = environment.getState();
        List<Double> action = agent.getOutput(state);
        environment.updateState(action);
        cartPanel.repaint();
    }

    private class CartPanel extends JPanel {
        private static final int CART_WIDTH = 50;
        private static final int CART_HEIGHT = 30;
        private static final int POLE_LENGTH = 100;
        private static final int TRACK_HEIGHT = 5;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set background to white for visibility
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Get current state
            var state = environment.getState();
            double cartPosition = state.get(0) * 2.4; // Denormalize from [-1,1] to actual position
            double poleAngle = state.get(2) * (12 * Math.PI / 180); // Denormalize to radians

            // Draw track
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, getHeight() - 50, getWidth(), TRACK_HEIGHT);

            // Calculate cart position in pixels
            int cartX = (int) ((double) getWidth() / 2 + cartPosition * ((double) getWidth() / 6));
            int cartY = getHeight() - 50 - CART_HEIGHT;

            // Draw cart
            g2d.setColor(Color.BLUE);
            g2d.fillRect(cartX - CART_WIDTH / 2, cartY, CART_WIDTH, CART_HEIGHT);

            // Draw pole
            g2d.setColor(Color.RED);
            AffineTransform old = g2d.getTransform();
            g2d.translate(cartX, cartY);
            g2d.rotate(-poleAngle); // Negative because Swing's Y-axis is inverted
            g2d.fillRect(-2, -POLE_LENGTH, 4, POLE_LENGTH);
            g2d.setTransform(old);

            // Draw status
            g2d.setColor(Color.BLACK);
            g2d.drawString("Cart Position: " + String.format("%.2f", cartPosition), 10, 20);
            g2d.drawString("Pole Angle: " + String.format("%.2f", Math.toDegrees(poleAngle)), 10, 40);
        }
    }
}