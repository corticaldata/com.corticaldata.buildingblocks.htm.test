package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.numenta.nupic.Parameters;
import org.numenta.nupic.algorithms.Classification;
import org.numenta.nupic.algorithms.SDRClassifier;
import org.numenta.nupic.algorithms.TemporalMemory;
import org.numenta.nupic.encoders.ScalarEncoder;
import org.numenta.nupic.model.ComputeCycle;
import org.numenta.nupic.model.Connections;
import org.numenta.nupic.util.ArrayUtils;

/**
 * Ejemplo de aprendizaje de secuencias de dígitos y predicción del siguiente dígito
 * utilizando Memoria Temporal Jerárquica (HTM).
 * 
 * @author paco
 */
public class WithoutSpatialPoolerDigitSequencePrediction {
	public static void main(String[] args) throws Exception {
		
		ScalarEncoder encoder = ScalarEncoder
				.builder()
				.n(201)
				.w(1)
				.minVal(-100.0)
				.maxVal(100.0)
				.periodic(false)
				.forced(true)
				.build();
		
		Connections memory = new Connections();
		Parameters.getAllDefaultParameters().apply(memory);
		
		TemporalMemory temporalMemory = new TemporalMemory();
		TemporalMemory.init(memory);
		
		SDRClassifier classifier = new SDRClassifier();
		
		Map<String, Object> classification = new HashMap<String, Object>();
		
		System.out.println("Input numbers from -100 to 100:");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		int count = 0;
		while (true) {
			count++;
			double value = Double.parseDouble(br.readLine());
			
			// Input through encoder
			int[] encoding = encoder.encode(value);
			int bucketIdx = encoder.getBucketIndices(value)[0];
			
			// Input through temporal memory
			int[] input = ArrayUtils.where(encoding, ArrayUtils.WHERE_1);
			ComputeCycle cc = temporalMemory.compute(memory, input, true);
			
			// Get the active cells for classifier input
			int[] activeCellIndexes = Connections.asCellIndexes(cc.activeCells()).stream().mapToInt(p -> p).sorted().toArray();
			
			classification.put("bucketIdx", bucketIdx);
			classification.put("actValue", value);
			Classification<Double> result = classifier.compute(count, classification, activeCellIndexes, true, true);
			
			System.out.println("Prediction: " + new BigDecimal(result.getMostProbableValue(1)).setScale(3, RoundingMode.HALF_EVEN));
		}
	}
}
