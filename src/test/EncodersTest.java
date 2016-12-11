package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.joda.time.DateTime;
import org.numenta.nupic.encoders.CategoryEncoder;
import org.numenta.nupic.encoders.DateEncoder;
import org.numenta.nupic.encoders.DeltaEncoder;
import org.numenta.nupic.encoders.ScalarEncoder;

public class EncodersTest {
	public static void main(String[] args) throws Exception {
		
		// Encoding a scalar
		ScalarEncoder scalarEncoder = ScalarEncoder
				.builder()
				.n(10)
				.w(3)
				.radius(1.0)
				.minVal(0.0)
				.maxVal(10.0)
				.periodic(true)
				.forced(true)
				.resolution(1)
				.build();
		
		int[] scalarEncoding = scalarEncoder.encode(5.0);
		System.out.println("ScalarEncoder Output = " + Arrays.toString(scalarEncoding));
		
		// Encoding a date
		DateEncoder dateEncoder = DateEncoder
				.builder()
				.dayOfWeek(3)
				.timeOfDay(5)
				.season(3)
				.periodic(true)
				.build();
		
		Calendar calendar = Calendar.getInstance();
		
		for (int i = 0; i < 1000; i++) {
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			DateTime dt = new DateTime(calendar.getTimeInMillis());
			System.out.println(dt.toString());
			int[] dateEncoding = dateEncoder.encode(dt);
			System.out.println("DateEncoder Output = " + Arrays.toString(dateEncoding));
		}
		
		// Encoding a category
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("NO_NEWS");
		categories.add("YELLOW_NEWS");
		categories.add("ORANGE_NEWS");
		categories.add("RED_NEWS");
		
		CategoryEncoder categoryEncoder = CategoryEncoder
				.builder()
				.w(3)
				.radius(1.0)
				.minVal(0.0)
				.maxVal(3.0)
				.periodic(false)
				.forced(true)
				.categoryList(categories)
				.build();

		int[] categoryEncoding = categoryEncoder.encode("Bike");
		System.out.println("CategoryEncoder Output = " + Arrays.toString(categoryEncoding));
		
		// Encoding a delta
		DeltaEncoder deltaEncoder = DeltaEncoder
				.deltaBuilder()
				.n(200)
				.w(21)
				.resolution(1)
				.minVal(-10)
				.maxVal(10)
				.build();
		
		double pips = 0;
		Random random = new Random(new Date().getTime());
		
		for (int i = 0; i < 10; i++) {
			if (random.nextDouble() > 0.5) {
				pips += 1;
			}
			else {
				pips -= 1;
			}
			System.out.println(pips);
			int[] deltaEncoding = deltaEncoder.encode(pips);
			System.out.println("DeltaEncoder Output = " + Arrays.toString(deltaEncoding));
		}
	}
}
