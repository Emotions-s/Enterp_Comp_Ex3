package utils;

public class Helper {
    
    public static boolean isValidateInput(String input) {
        return input.matches("\\d+,\\d+");
        
    }

    public static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }

        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }

        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }

        return true;
    }

    public static int numberOfPrimeInRange(int start, int end) {
        int i = 0;
        while (start <= end) {
            if (isPrime(start))
                i++;
            start++;
        }
        return i;
    }
}
