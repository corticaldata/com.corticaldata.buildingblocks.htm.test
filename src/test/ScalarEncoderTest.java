package test;

import java.util.Arrays;

import org.numenta.nupic.encoders.ScalarEncoder;

public class ScalarEncoderTest {
	public static void main(String[] args) throws Exception {
		ScalarEncoder encoder = ScalarEncoder
				.builder()
				.n(20)
				.w(1)
				.minVal(-10.0)
				.maxVal(9.0)
				.periodic(false)
				.forced(true)
				.build();
		
		int[] encoding = encoder.encode(0.0);
		System.out.println("ScalarEncoder Output = " + Arrays.toString(encoding));
		
		encoding = encoder.encode(1.0);
		System.out.println("ScalarEncoder Output = " + Arrays.toString(encoding));
		
		encoding = encoder.encode(1.5);
		System.out.println("ScalarEncoder Output = " + Arrays.toString(encoding));
		
		encoding = encoder.encode(2.0);
		System.out.println("ScalarEncoder Output = " + Arrays.toString(encoding));
		
		encoding = encoder.encode(8.0);
		System.out.println("ScalarEncoder Output = " + Arrays.toString(encoding));
		
		encoding = encoder.encode(9.0);
		System.out.println("ScalarEncoder Output = " + Arrays.toString(encoding));
	}
}
