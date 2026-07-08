//DEPS ch.qos.logback:logback-classic:1.5.18
//DEPS org.cups4j:cups4j:0.8.2
//DEPS org.slf4j:slf4j-api:2.0.17

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Printer {
	private static final Logger LOG = LoggerFactory.getLogger(Printer.class);

	private CupsClient cupsClient;
	private CupsPrinter cupsPrinter;

	public Printer() {
		cupsClient = new CupsClient();

		try {
			cupsPrinter = cupsClient.getDefaultPrinter();
		} catch (Exception e) {
			LOG.error("Failed to get default printer");
			System.exit(1);
		}
	}

	private void print(FileInputStream fis) throws IOException {
		PrintJob printJob = new PrintJob.Builder(fis).build();
		cupsPrinter.print(printJob);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: jbang Printer.java <uri> <file_path>");
			return;
		}

		FileInputStream fis;
		try {
			fis = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			LOG.error("File not found");
			return;
		}

		Printer printer = new Printer();
		try {
			printer.print(fis);
		} catch (IOException e) {
			LOG.error("Printing error");
		}
	}
}