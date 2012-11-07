package com.cloudera.iterativereduce.yarn;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import com.cloudera.iterativereduce.ComputableMaster;

/*
 * Useless standalone, used for tests
 */
public class CompoundAdditionMaster implements ComputableMaster<UpdateableInt> {
  private static final Log LOG = LogFactory.getLog(CompoundAdditionMaster.class);
  
  private UpdateableInt masterTotal;
  
  @Override
  public UpdateableInt compute(Collection<UpdateableInt> workerUpdates,
      Collection<UpdateableInt> masterUpdates) {

    int total = 0;
    
    for (UpdateableInt i : workerUpdates) {
      total += i.get();
    }
    
    for (UpdateableInt i : masterUpdates) {
      total += i.get();
    }
    
    //if (masterTotal == null)
      masterTotal = new UpdateableInt();
    
    masterTotal.set(total);
    LOG.debug("Current total=" + masterTotal.get() 
        + ", workerUpdates=" + toStrings(workerUpdates) 
        + ", masterUpdates=" + toStrings(masterUpdates));
    
    return masterTotal;
  }

  private String toStrings(Collection<UpdateableInt> c) {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    
    for (UpdateableInt i : c) {
      sb.append(i.get()).append(", ");
    }

    sb.append("]");
    return sb.toString();
    
  }
  @Override
  public UpdateableInt getResults() {
    return masterTotal;
  }

  @Override
  public void setup(Configuration c) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void complete(DataOutputStream out) throws IOException {
    out.write(String.valueOf(masterTotal.get()).getBytes());
  }
}
