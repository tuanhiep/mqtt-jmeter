package net.xmeter.functions;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

public class PayloadFunc extends AbstractFunction{
	private static final List<String> desc = new LinkedList<String>();
	private int MIN_PARA_COUNT = 1;
	private int MAX_PARA_COUNT = 1;
	
    static {
        desc.add("Get a random payload with specified length.");
    }
    //function名称
    private static final String KEY = "__PayloadFunc";

    private SecureRandom random = new SecureRandom();
    private static char[] seeds = "abcdefghijklmnopqrstuvwxmy0123456789".toCharArray();
    private Object[] values;

    public List<String> getArgumentDesc() {
        return desc; 
    }
    
    @Override
	public String execute(SampleResult arg0, Sampler arg1) throws InvalidVariableException {
    	int max = new Integer(((CompoundVariable) values[0]).execute().trim());
		StringBuffer res = new StringBuffer();
		for(int i = 0; i < max; i++) {
			res.append(seeds[random.nextInt(seeds.length - 1)]);
		}
		return res.toString();
	}
    
    @Override
	public String getReferenceKey() {
		return KEY;
	}

	@Override
	public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
		 checkParameterCount(parameters, MIN_PARA_COUNT, MAX_PARA_COUNT);
		 values = parameters.toArray();
	}
}
