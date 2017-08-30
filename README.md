# Jenkins Library

This is our take on the Jenkins workflow pipeline. The overall goal is to find the right balance between reuse and individuality. The libraries pattern is designed to provide quick access to commonly used functionality for all your projects, while allowing easy extensibility.

# Usage

### General structure

To get started, check out a typical Jenkinsfile you would have in one of your projects when using this Library:

```Groovy
//import Library (you need to configure the library in your jenkins settings once)
@Library("jenkins-library-base")
import com.ibm.oip.jenkins.*;

//specify a pipeline for the master branch
Pipeline master = new PipelineBuilder().forMaster().withSteps([
  Common.CHECKOUT,
  Gradle.ASSEMBLE,
  Gradle.UNIT_TEST,
  Common.TRIGGER_DEPLOYMENT('test')
]).build();

//actually run the pipeline
new PipelineRunner()
  .withScriptEngine(this)         //passing in the current context
  .withBranch(env.BRANCH_NAME)    //passing in the branch to build
  .withPossiblePipelines(master)
  .run();
```

You can see that a lot of the actual build logic is hidden behind these predefined steps, which are only composed into a pipeline here. This is great, because you can centralize the logic which you might reuse in a lot of projects. What happens when you need a special build step in a project and you do not already have this step in your central library or probably never want to move it there? We got you covered:

### Custom steps

```Groovy
Pipeline master = new PipelineBuilder().forMaster().withSteps([
  Common.CHECKOUT,
  new Step(){
    void doStep(BuildContext buildContext) {
        buildContext.getScriptEngine().sh "echo 'my custom logic'"
   }
]).build();
```

By implementing the interface `com.ibm.oip.jenkins.steps.Step` you can build custom steps and feed them into the pipeline.
        
### Multiple Branches / Pipelines

Normally, when trying to have different logic for different branches your `Jenkinsfile` can become very cluttered as you will usually work with big if-clauses. With this library we are trying to ease that process by defining multiple pipelines that know what they can build - the `PipelineRunner` will than check with every of them if they can build the current branch.

```Groovy
@Library("jenkins-library-base")
import com.ibm.oip.jenkins.*;

Pipeline master = new PipelineBuilder().forMaster().withSteps([
  Common.CHECKOUT,
  Gradle.ASSEMBLE,
  Common.TRIGGER_DEPLOYMENT('intern')
]).build();

Pipeline pullRequest = new PipelineBuilder().forAnyButMaster().withSteps([
  Common.CHECKOUT,
  Gradle.ASSEMBLE,
  Gradle.UNIT_TEST,
  Gradle.STATIC_ANALYSIS_PR
]).build();

new PipelineRunner()
  .withScriptEngine(this)
  .withBranch(env.BRANCH_NAME)
  .withPossiblePipelines(master, pullRequest)
  .run();

```
