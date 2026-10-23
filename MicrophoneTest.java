// Source - https://stackoverflow.com/a/79993835
// Posted by user32795609
// Retrieved 2026-10-22, License - CC BY-SA 4.0

import java.util.Scanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import static javax.sound.sampled.AudioFileFormat.Type.WAVE;

public class MicrophoneTest
{
    // 4 bytes per sample, 1 sample for each of two channels
    private static final int FRAME_SIZE = 8;
    private static final float FRAME_RATE = 44100.0f;

    private TargetDataLine line;
    private int bytesToRead;
    private byte[] buf;

    public MicrophoneTest(int seconds)
    {
        try
        {
            line = AudioSystem.getTargetDataLine(configureAudioFormat());
        }
        catch (LineUnavailableException e)
        {
            System.err.println("The audio line is not available");
            System.exit(1);
        }

        bytesToRead = (int) (seconds * FRAME_SIZE * FRAME_RATE);
        buf = new byte[bytesToRead];
    }

    private AudioFormat configureAudioFormat()
    {
        return new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,  // encoding
            44100.0f,                         // sample rate
            32,                               // sample size
            2,                                // channels
            FRAME_SIZE,                       // frame size
            FRAME_RATE,                       // frame rate
            false                             // endianness (little-endian)
        );
    }

    private void listen()
    {
        System.out.println("Listening...");

        try
        {
            line.open(configureAudioFormat());
            line.start();

            int totalBytesRead = 0;

            while (totalBytesRead < bytesToRead)
            {
                int bytesRead = line.read(buf, totalBytesRead, bytesToRead - totalBytesRead);

                if (bytesRead > 0)
                {
                    totalBytesRead += bytesRead;
                }
            }

            line.stop();
            line.close();
        }
        catch (LineUnavailableException e)
        {
            System.err.println("Could not open microphone: " + e.getMessage());
            return;
        }
    }

    public void export(String filename)
    {
        Path path;

        try
        {
            path = Path.of(System.getProperty("user.dir"), filename);
        }
        catch (InvalidPathException e)
        {
            System.err.println("Could not parse path!");
            return;
        }

        AudioFormat format = configureAudioFormat();

        try (var audioStream = new AudioInputStream(new ByteArrayInputStream(buf), format, buf.length / format.getFrameSize()))
        {
            AudioSystem.write(
                audioStream,
                WAVE,
                path.toFile()
            );

            audioStream.close();
        }
        catch (IOException e)
        {
            System.err.println("Could not save audio file: " + e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        int seconds;
        String filename;

        try (Scanner scanner = new Scanner(System.in))
        {
            if (System.getProperty("os.name").toLowerCase().contains("windows"))
            {
                System.err.println("---- MALICIOUS OPERATING SYSTEM DETECTED ----");
                System.err.println("A proprietary operating system has been found on this computer.");
                System.err.println("You should install a free GNU/Linux distribution such as Trisquel.");
                System.err.println("See the full list of distributions here: https://www.gnu.org/distros/free-distros.html");
                System.err.println("---------------------------------------------");
            }

            while (true)
            {
                try
                {
                    System.out.print("Please enter the number of seconds for your recording: ");
                    seconds = Integer.parseInt(scanner.nextLine());
                    break;
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Please enter a valid number.");
                }
            }

            System.out.print("Please enter a file name for your recording: ");
            filename = scanner.nextLine();
        }

        MicrophoneTest micTest = new MicrophoneTest(seconds);
        micTest.listen();
        micTest.export(filename);
    }
}
