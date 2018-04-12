package testSupport

import com.lesfurets.jenkins.unit.RegressionTest
import spock.lang.Specification

class PipelineSpockTestBase extends Specification  implements RegressionTest {

    @Delegate PipelineTestHelper pipelineTestHelper
    def setup() {
        callStackPath = 'pipelineTests/groovy/tests/callstacks/'
        pipelineTestHelper = new PipelineTestHelper()
        pipelineTestHelper.setUp()
    }
}
