File compositeArtifactsFile = new File( basedir, "target/compositerepo/compositeArtifacts.xml" );
File compositeContentFile = new File( basedir, "target/compositerepo/compositeContent.xml" );
File p2IndexFile = new File( basedir, "target/compositerepo/p2.index" );

assert compositeArtifactsFile.isFile()
assert compositeContentFile.isFile()
assert p2IndexFile.isFile()
