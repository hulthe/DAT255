package main.java.it.chalmers.digit;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import static java.lang.System.out;

public final class Mopy implements MopedController {

	//Replace path with your own path to the python scripts folder
	private static final String PATH = "/path/to/python_scripts/";

	private final Executor executor;

	public Mopy(Executor executor) {
		this.executor = executor;
	}

	public Future<String> getSpeed() {
		RunnableFuture<String> future = new FutureTask<>(() -> runCommand(Command.GET_SPEED));
		executor.execute(future);
		return future;
	}

	@Override
	public Future<Void> setSpeed(int speed) {
		throw new NotImplementedException();
	}

	@Override
	public Future<Void> steer(double steeringVector) {
		throw new NotImplementedException();
	}

	@Override
	public Future<String> getFrontSensorDistance() {
		return null;
	}

	public Future<Void> stop() {
		throw new NotImplementedException();
	}

	/**
	 * Runs a python file with {@code command}
	 * @param command
	 * @param args
	 * @return
	 * @throws IOException
	 */
	private static String runCommand(Command command, String... args) throws IOException {
		// Creates the command string
		StringJoiner joiner = new StringJoiner(" ");
		joiner.add("python");
		joiner.add(PATH + command.toString());

		for (String argument : args) {
			joiner.add(argument);
		}

		// Creates the process and tries to run it
		Process p;
		try {
			p = Runtime.getRuntime().exec(joiner.toString());
		} catch (IOException e) {
			throw new IOException("Couldn't execute python command on runtime.", e);
		}

		// Initializes the commands output and error streams
		BufferedReader stdInput = new BufferedReader(new
				InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
				InputStreamReader(p.getErrorStream()));

		// Read the output from the command
		out.printf("Here is the standard output of the command:\n");
		String outText;
		try {
			while ((outText = stdInput.readLine()) != null) { }
		}catch (IOException ex){
			throw new IOException("Couldn't read from standard input stream after execution of python command.", ex);
		}

		// Read any errors from the attempted command
		out.println("Here is the standard error of the command (if any):\n");
		String errorText;
		try {
			while ((errorText = stdError.readLine()) != null) { }
		}catch (IOException ex){
			throw new IOException("Couldn't read from standard error stream after execution of python command.", ex);
		}

		// If there was an error then throw an exception to let the caller know that something went wrong
		if(errorText != null) {
			out.printf("%s\n", errorText);
			throw new IllegalStateException(String.format("Command: \"%s\" ran with errors:\n%s", joiner.toString(), errorText));
		}

		return outText;
	}

	private enum Command {
		GET_SPEED("get_speed.py"),
		SET_SPEED("set_speed.py"),
		DRIVE("drive.py"),
		STOP("stop.py"),
		STEER("steer.py");

		private final String command;

		Command(String filename, String... args) {
			this.command = filename;
		}

		@Override
		public String toString() {
			return this.command;
		}
	}

}
