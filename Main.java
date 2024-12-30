import java.math.BigInteger;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            StringBuilder input = new StringBuilder();
            int braceCount = 0;
            boolean insideQuotes = false;
            
            while ((line = reader.readLine()) != null) {
                for (char c : line.toCharArray()) {
                    if (c == '"') {
                        insideQuotes = !insideQuotes;
                    }
                    if (!insideQuotes) {
                        if (c == '{') braceCount++;
                        if (c == '}') braceCount--;
                    }
                    input.append(c);
                }
                
                // If we have a complete JSON (matching braces), process it
                if (braceCount == 0 && input.length() > 0) {
                    BigInteger result = findSecret(input.toString());
                    System.out.println(result);
                    break;  // Exit after processing
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static BigInteger findSecret(String jsonInput) throws Exception {
        Map<String, Map<String, String>> data = new HashMap<>();
        jsonInput = jsonInput.trim();
        
        // Get k value first
        String kPattern = "\"k\":\\s*(\\d+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(kPattern);
        java.util.regex.Matcher matcher = pattern.matcher(jsonInput);
        if (!matcher.find()) {
            throw new Exception("k value not found");
        }
        int k = Integer.parseInt(matcher.group(1));
        
        // Parse points
        List<Point> points = new ArrayList<>();
        String pointPattern = "\"(\\d+)\":\\s*\\{\\s*\"base\":\\s*\"(\\d+)\"\\s*,\\s*\"value\":\\s*\"([^\"]+)\"\\s*\\}";
        pattern = java.util.regex.Pattern.compile(pointPattern);
        matcher = pattern.matcher(jsonInput);
        
        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int base = Integer.parseInt(matcher.group(2));
            String value = matcher.group(3);
            BigInteger y = new BigInteger(value, base);
            points.add(new Point(BigInteger.valueOf(x), y));
        }
        
        // Sort points by x value
        Collections.sort(points, (a, b) -> a.x.compareTo(b.x));
        
        // Take only k points for interpolation
        points = points.subList(0, k);
        
        // Perform Lagrange interpolation to find constant term
        return lagrangeInterpolation(points, BigInteger.ZERO);
    }
    
    static class Point {
        BigInteger x, y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
    
    private static BigInteger lagrangeInterpolation(List<Point> points, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        
        for (int i = 0; i < points.size(); i++) {
            BigInteger term = points.get(i).y;
            
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger numerator = x.subtract(points.get(j).x);
                    BigInteger denominator = points.get(i).x.subtract(points.get(j).x);
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            
            result = result.add(term);
        }
        
        return result;
    }
}
