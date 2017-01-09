package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.numenta.nupic.encoders.CategoryEncoder;
import org.numenta.nupic.encoders.DateEncoder;
import org.numenta.nupic.encoders.MultiEncoder;
import org.numenta.nupic.util.ArrayUtils;

public class MultiEncoderTest {
	public static void main(String[] args) throws Exception {
		
		// Date encoder
		DateEncoder dateEncoder = DateEncoder
				.builder()
				.dayOfWeek(3)
				.timeOfDay(5)
				.season(3)
				.periodic(true)
				.build();
		
		// Category encoder
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("UP");
		categories.add("DOWN");
		
		CategoryEncoder categoryEncoder = CategoryEncoder
				.builder()
				.w(3)
				.radius(1.0)
				.minVal(0.0)
				.maxVal(1.0)
				.periodic(false)
				.forced(true)
				.categoryList(categories)
				.build();
		
		MultiEncoder multiEncoder = MultiEncoder
				.builder()
				.build();
		
		multiEncoder.addEncoder("category", categoryEncoder);
		multiEncoder.addEncoder("date", dateEncoder);
		
		int[] categoryEncoding = categoryEncoder.encode("UP");
		System.out.println("Category: " + Arrays.toString(categoryEncoding));
		
		int[] dateEncoding = dateEncoder.encode(new DateTime(new Date().getTime()));
		System.out.println("Date: " + Arrays.toString(dateEncoding));
		
		int[] categoryEncoding2 = multiEncoder.encodeField("category", "UP");
		int[] dateEncoding2 = multiEncoder.encodeField("date", new DateTime(new Date().getTime()));
		System.out.println("Category: " + Arrays.toString(categoryEncoding2));
		System.out.println("Date: " + Arrays.toString(dateEncoding2));
		
		HashMap<String, Object> input = new HashMap<String, Object>();
		input.put("category", "UP");
		DateTime dt = new DateTime(new Date().getTime());
		input.put("date", dt);
		
		int[] encoding = multiEncoder.encode(input);
		System.out.println("Encoding: " + Arrays.toString(encoding));
		
	}
}
