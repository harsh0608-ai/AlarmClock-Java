import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AlarmClock {

    private static final DateTimeFormatter[] ACCEPTED_FORMATS = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("H:mm:ss"),
        DateTimeFormatter.ofPattern("H:mm")
    };

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Simple Java Alarm Clock ===");
        System.out.print("Enter alarm time (HH:mm or HH:mm:ss, 24-hour): ");
        String input = sc.nextLine().trim();

        LocalTime alarmTime = parseTime(input);
        if (alarmTime == null) {
            System.out.println("Couldn't parse the time. Use formats like 07:30 or 07:30:00");
            sc.close();
            return;
        }

        System.out.println("Alarm set for: " + alarmTime);
        System.out.println("Press Ctrl+C to exit.");

        while (true) {
            LocalTime now = LocalTime.now().withNano(0);
            System.out.print("\rCurrent time: " + now + "    ");

            if (!now.isBefore(alarmTime)) {
                System.out.println("\nAlarm time reached: " + now);
                playWav("alarm.wav");
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        sc.close();
    }

    private static LocalTime parseTime(String s) {
        for (DateTimeFormatter fmt : ACCEPTED_FORMATS) {
            try {
                return LocalTime.parse(s, fmt);
            } catch (DateTimeParseException e) {
            }
        }
        return null;
    }

    private static void playWav(String filePath) {
        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            System.err.println("Alarm sound not found at: " + filePath);
            return;
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            while (clip.isActive()) {
                Thread.sleep(5000);
            }
            clip.close();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Could not play sound: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
