import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Vault
{

	private int[] splitAsInt(String text, String delimiter)
	{
		String[] split = text.split(delimiter);
		int[] intArr = new int[split.length];

		for (int i = 0; i < split.length; i++)
		{
			try
			{
				intArr[i] = Integer.parseInt(split[i]);
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException("One of your numbers is not valid!");
			}
		}

		return intArr;
	}

	private int[] codeToSequence(String code)
	{
		if (code.contains(", "))
			return splitAsInt(code, ", ");
		else if (code.contains(","))
			return splitAsInt(code, ",");
		else
			throw new IllegalArgumentException("Your code should have a comma!");
	}

	private int average(int[] arr)
	{
		int sum = 0;
		for (int i = 0; i < arr.length; i++)
			sum += arr[i];

		return sum / arr.length;
	}

	private int largestConsecutiveGap(int[] arr)
	{
		int maxGap = 0;
		for (int i = 0; i < arr.length - 1; i++)
		{
			int gap = Math.abs(arr[i + 1] - arr[i]);
			if (gap > maxGap)
				maxGap = gap;
		}

		return maxGap;
	}

	private void printStatistics(int[] sortedArray)
	{
		System.out.println("Largest number: " + sortedArray[sortedArray.length - 1]);
		System.out.println("Smallest number: " + sortedArray[0]);
		System.out.println("Average number: " + average(sortedArray));
		System.out.println("Largest gap between consecutive numbers: " + largestConsecutiveGap(sortedArray));
	}

	/*
	 * There is no need for a "duplicate values found" error. If all the numbers are
	 * only increasing, they will implicitly be unique.
	 */
	private String isStableSequence(int[] sequence)
	{
		StringBuilder result = new StringBuilder();
		PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());

		boolean success = true;
		int lastNumber = sequence[0];

		for (int i = 0; i < sequence.length; i++)
		{
			int num = sequence[i];
			if (i > 0)
			{
				if (num - lastNumber > 5)
				{
					result.append("Difference between consecutive numbers greater than five!\n");
					success = false;
				}

				if (num <= lastNumber)
				{
					result.append("Numbers not in increasing order!\n");
					success = false;
				}
			}

			lastNumber = num;
			pq.add(num);
		}

		int[] sorted = new int[pq.size()];
		int index = sorted.length - 1;
		while (!pq.isEmpty())
			sorted[index--] = pq.poll();

		printStatistics(sorted);

		if (success)
			result.append("Successfully opened!\n");

		return result.toString();
	}

	public static void main(String[] args)
	{
		Vault vault = new Vault();
		try (Scanner scanner = new Scanner(System.in))
		{
			int[] sequence;

			while (true)
			{
				System.out.print("Enter the code to unlock the vault: ");
				String code = scanner.nextLine();

				try
				{
					sequence = vault.codeToSequence(code);
					break;
				}
				catch (IllegalArgumentException e)
				{
					System.out.println(e.getMessage());
					continue;
				}
			}

			System.out.println(vault.isStableSequence(sequence));
		}
	}
}