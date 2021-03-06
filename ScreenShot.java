import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 * 外部使用
 * 定时截图
 */
public class ScreenShot {
    private static IO io = IO.getInstance();
    private static Config config = io.getConfig();

    private static int num = 0;
    private static int maxNum = 10000;
    private static boolean start = false;

    private static int x = 50;
    private static int y = 61;
    private static int width = config.getWidth();
    private static int height = config.getHeight();
    private static String suffix = "bmp";
    private static int period = config.getDelay() / 3;
    private static Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    public static void main(String[] args) {
        Timer timer = new Timer(period, e -> {
            screenShot();
        });
        timer.start();
    }

    public static void kill() {
        maxNum = 0;
    }

    private static void screenShot() {
        num++;
        String path = io.getFileName(suffix, true);

        try {
            if (num % 8 == 0) {
                RenderedImage im = new Robot()
                        .createScreenCapture(new Rectangle(0, 0, d.width, d.height))
                        .getSubimage(x, y, Math.min(width, d.width - x), Math.min(height, d.height - y));
                ImageIO.write(im, suffix, new File(path));

                io.print("截图:第" + num + "张 " + path.substring(config.getDefaultPath().length()));

                boolean check = check(im);
                if (check) {
                    start = true;
                }
                if (start && !check) {
                    maxNum = 0;
                }
            } else {
                ImageIO.write(new Robot()
                        .createScreenCapture(new Rectangle(0, 0, d.width, d.height))
                        .getSubimage(x, y, Math.min(width, d.width - x), Math.min(height, d.height - y)), suffix, new File(path));

                io.print("截图:第" + num + "张 " + path.substring(config.getDefaultPath().length()));
            }
        } catch (IOException | AWTException e) {
            io.printErr("screen shot 截图失败");
        }
        if (num >= maxNum) {
            io.end();
        }
    }

    private static boolean check(RenderedImage im) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(im, "bmp", baos);
        } catch (IOException e) {
            io.printErr("screen shot 检测失败");
        }
        byte[] data = baos.toByteArray();

        for (int i = 0; i < width; i++) {
            if (!Outside.accept(data[(int) (Math.random() * width * height * 3) + 54])) {
                return false;
            }
        }
        return true;
    }
}