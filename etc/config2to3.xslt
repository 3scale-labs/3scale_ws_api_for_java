<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns="urn:jboss:jbosscache-core:config:3.2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 <xsl:output method="xml" indent="yes" version="1.0" encoding="UTF-8" omit-xml-declaration="no"/>
 <xsl:template match="/">
    <xsl:element name="jbosscache">
       <xsl:call-template name="locking"/>
       <xsl:call-template name="transaction"/>
       <xsl:call-template name="serialization"/>
       <xsl:call-template name="startup"/>
       <xsl:apply-templates select="//attribute"/>
    </xsl:element>
 </xsl:template>

 <xsl:template match="//attribute"/>

 <xsl:template name="locking">
    <xsl:if
          test="//attribute[@name='IsolationLevel'] | //attribute[@name='LockAcquisitionTimeout'] | //attribute[@name='LockParentForChildInsertRemove']">
       <xsl:element name="locking">
          <xsl:if test="//attribute[@name='IsolationLevel']">
             <xsl:attribute name="isolationLevel">
                <xsl:value-of select="normalize-space(//attribute[@name='IsolationLevel'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='LockAcquisitionTimeout']">
             <xsl:attribute name="lockAcquisitionTimeout">
                <xsl:value-of select="normalize-space(//attribute[@name='LockAcquisitionTimeout'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='LockParentForChildInsertRemove']">
             <xsl:attribute name="lockParentForChildInsertRemove">
                <xsl:value-of select="normalize-space(//attribute[@name='LockParentForChildInsertRemove'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='NodeLockingScheme']">
             <xsl:attribute name="nodeLockingScheme">
                <xsl:value-of select="normalize-space(//attribute[@name='NodeLockingScheme'])"/>
             </xsl:attribute>
          </xsl:if>
       </xsl:element>
    </xsl:if>
 </xsl:template>

 <xsl:template name="transaction">
    <xsl:if
          test="//attribute[@name='TransactionManagerLookupClass'] | //attribute[@name='SyncRollbackPhase'] | //attribute[@name='SyncCommitPhase']">
       <xsl:element name="transaction">
          <xsl:if test="//attribute[@name='TransactionManagerLookupClass']">
             <xsl:attribute name="transactionManagerLookupClass">
                <xsl:value-of select="normalize-space(//attribute[@name='TransactionManagerLookupClass'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='SyncCommitPhase']">
             <xsl:attribute name="syncCommitPhase">
                <xsl:value-of select="normalize-space(//attribute[@name='SyncCommitPhase'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='SyncRollbackPhase']">
             <xsl:attribute name="syncRollbackPhase">
                <xsl:value-of select="normalize-space(//attribute[@name='SyncRollbackPhase'])"/>
             </xsl:attribute>
          </xsl:if>
       </xsl:element>
    </xsl:if>
 </xsl:template>

 <xsl:template match="//attribute[@name='CacheMode']">
       <xsl:if test="(normalize-space(.) = 'INVALIDATION_ASYNC') or (normalize-space(.) = 'INVALIDATION_SYNC')">
          <clustering mode="invalidation">
             <xsl:if test="//attribute[@name='ClusterName']">
                <xsl:attribute name="clusterName">
                   <xsl:value-of select="normalize-space(//attribute[@name='ClusterName'])"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="(normalize-space(.) = 'INVALIDATION_ASYNC')">
                <xsl:element name="async">
                   <xsl:call-template name="asyncAttributes"/>
                </xsl:element>
             </xsl:if>
             <xsl:if test="(normalize-space(.) = 'INVALIDATION_SYNC')">
                <xsl:element name="sync">
                   <xsl:call-template name="syncAttributes"/>
                </xsl:element>
             </xsl:if>
             <xsl:call-template name="stateRetrieval" />
             <xsl:call-template name="transport"/>
          </clustering>
       </xsl:if>
       <xsl:if test="(normalize-space(.) = 'REPL_ASYNC') or (normalize-space(.) = 'REPL_SYNC')">
          <clustering mode="replication">
             <xsl:if test="//attribute[@name='ClusterName']">
                <xsl:attribute name="clusterName">
                   <xsl:value-of select="normalize-space(//attribute[@name='ClusterName'])"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="(normalize-space(.) = 'REPL_ASYNC')">
                <xsl:element name="async">
                   <xsl:call-template name="asyncAttributes"/>
                </xsl:element>
             </xsl:if>
             <xsl:if test="(normalize-space(.) = 'REPL_SYNC')">
                <xsl:element name="sync">
                   <xsl:call-template name="syncAttributes"/>
                </xsl:element>
             </xsl:if>
             <xsl:call-template name="buddy"/>
             <xsl:call-template name="stateRetrieval" />
             <xsl:call-template name="transport"/>
          </clustering>
       </xsl:if>
 </xsl:template>

 <xsl:template name="serialization">
    <xsl:if
          test="//attribute[@name='ObjectInputStreamPoolSize'] | //attribute[@name='ObjectOutputStreamPoolSize'] | //attribute[@name='ReplicationVersion'] | //attribute[@name='MarshallerClass'] | //attribute[@name='UseLazyDeserialization'] | //attribute[@name='UseRegionBasedMarshalling']">
       <serialization>
          <xsl:if test="//attribute[@name='ObjectInputStreamPoolSize']">
             <xsl:attribute name="objectInputStreamPoolSize">
                <xsl:value-of select="normalize-space(//attribute[@name='ObjectInputStreamPoolSize'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='ObjectOutputStreamPoolSize']">
             <xsl:attribute name="objectOutputStreamPoolSize">
                <xsl:value-of select="normalize-space(//attribute[@name='ObjectOutputStreamPoolSize'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='ReplicationVersion']">
             <xsl:attribute name="version">
                <xsl:value-of select="normalize-space(//attribute[@name='ReplicationVersion'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='MarshallerClass']">
             <xsl:attribute name="marshallerClass">
                <xsl:value-of select="normalize-space(//attribute[@name='MarshallerClass'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='UseLazyDeserialization']">
             <xsl:attribute name="useLazyDeserialization">
                <xsl:value-of select="normalize-space(//attribute[@name='UseLazyDeserialization'])"/>
             </xsl:attribute>
          </xsl:if>

          <xsl:if test="//attribute[@name='UseRegionBasedMarshalling']">
             <xsl:attribute name="useRegionBasedMarshalling">
                <xsl:value-of select="normalize-space(//attribute[@name='UseRegionBasedMarshalling'])"/>
             </xsl:attribute>
          </xsl:if>
       </serialization>
    </xsl:if>
 </xsl:template>

 <xsl:template name="buddy">
    <xsl:if test="//attribute[@name='BuddyReplicationConfig']">
       <buddy>
          <xsl:if test="//buddyReplicationEnabled">
             <xsl:attribute name="enabled">
                <xsl:value-of select="//buddyReplicationEnabled"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//buddyPoolName">
             <xsl:attribute name="poolName">
                <xsl:value-of select="//buddyPoolName"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//buddyCommunicationTimeout">
             <xsl:attribute name="communicationTimeout">
                <xsl:value-of select="//buddyCommunicationTimeout"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//autoDataGravitation | //dataGravitationRemoveOnFind | //dataGravitationSearchBackupTrees">
             <dataGravitation>
                <xsl:if test="//autoDataGravitation">
                   <xsl:attribute name="auto">
                      <xsl:value-of select="//autoDataGravitation"/>
                   </xsl:attribute>
                </xsl:if>
                <xsl:if test="//dataGravitationRemoveOnFind">
                   <xsl:attribute name="removeOnFind">
                      <xsl:value-of select="//dataGravitationRemoveOnFind"/>
                   </xsl:attribute>
                </xsl:if>
                <xsl:if test="//dataGravitationSearchBackupTrees">
                   <xsl:attribute name="searchBackupTrees">
                      <xsl:value-of select="//dataGravitationSearchBackupTrees"/>
                   </xsl:attribute>
                </xsl:if>
             </dataGravitation>
          </xsl:if>
          <xsl:if test="//buddyLocatorProperties">
             <locator>
                <xsl:if test="//buddyLocatorClass">
                   <xsl:attribute name="class">
                      <xsl:value-of select="//buddyLocatorClass"/>
                   </xsl:attribute>
                </xsl:if>
                <xsl:if test="//buddyLocatorProperties">
                   <properties>
                      <xsl:value-of select="//buddyLocatorProperties"/>
                   </properties>
                </xsl:if>
             </locator>
          </xsl:if>
       </buddy>
    </xsl:if>
 </xsl:template>

 <xsl:template name="asyncAttributes">
    <xsl:if test="//attribute[@name='UseReplQueue']">
       <xsl:attribute name="useReplQueue">
          <xsl:value-of select="//attribute[@name='UseReplQueue']"/>
       </xsl:attribute>
    </xsl:if>
    <xsl:if test="//attribute[@name='ReplQueueInterval']">
       <xsl:attribute name="replQueueInterval">
          <xsl:value-of select="//attribute[@name='ReplQueueInterval']"/>
       </xsl:attribute>
    </xsl:if>
    <xsl:if test="//attribute[@name='ReplQueueMaxElements']">
       <xsl:attribute name="replQueueMaxElements">
          <xsl:value-of select="//attribute[@name='ReplQueueMaxElements']"/>
       </xsl:attribute>
    </xsl:if>
 </xsl:template>

 <xsl:template name="syncAttributes">
    <xsl:if test="//attribute[@name='SyncReplTimeout']">
       <xsl:attribute name="replTimeout">
          <xsl:value-of select="//attribute[@name='SyncReplTimeout']"/>
       </xsl:attribute>
    </xsl:if>
 </xsl:template>

 <xsl:template name="startup">
    <xsl:if test="//attribute[@name='InactiveOnStartup']">
       <xsl:element name="startup">
          <xsl:attribute name="regionsInactiveOnStartup">
             <xsl:value-of select="normalize-space(//attribute[@name='InactiveOnStartup'])"/>
          </xsl:attribute>
       </xsl:element>
    </xsl:if>
 </xsl:template>

 <xsl:template name="stateRetrieval">
    <xsl:if
          test="//attribute[@name='FetchInMemoryState'] | //attribute[@name='StateRetrievalTimeout']">
       <xsl:element name="stateRetrieval">
          <xsl:if test="//attribute[@name='FetchInMemoryState']">
             <xsl:attribute name="fetchInMemoryState">
                <xsl:value-of select="normalize-space(//attribute[@name='FetchInMemoryState'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='StateRetrievalTimeout']">
             <xsl:attribute name="timeout">
                <xsl:value-of select="normalize-space(//attribute[@name='StateRetrievalTimeout'])"/>
             </xsl:attribute>
          </xsl:if>
       </xsl:element>
    </xsl:if>
 </xsl:template>

 <xsl:template match="//attribute[@name='ShutdownHookBehavior']">
    <shutdown>
       <xsl:attribute name="hookBehavior">
          <xsl:value-of select="normalize-space(.)"/>
       </xsl:attribute>
    </shutdown>
 </xsl:template>

 <xsl:template match="//attribute[@name='ExposeManagementStatistics']">
    <jmxStatistics>
       <xsl:attribute name="enabled">
          <xsl:value-of select="normalize-space(.)"/>
       </xsl:attribute>
    </jmxStatistics>
 </xsl:template>

 <xsl:template match="//attribute[@name='EvictionPolicyConfig']">
    <eviction>
       <xsl:if test="./config/attribute[@name='wakeUpIntervalSeconds']">
          <xsl:attribute name="wakeUpInterval">
             <xsl:value-of
                   select="concat(normalize-space(./config/attribute[@name='wakeUpIntervalSeconds']), '000')"/>
          </xsl:attribute>
       </xsl:if>
       <xsl:if test="./config/region[@name='/_default_']">
          <default>
             <xsl:choose>
                <xsl:when test="./config/region[@name='/_default_' and @policyClass]">
                   <xsl:if test="./config/region[@name='/_default_' and not(starts-with(@policyClass,'org.jboss.cache.eviction'))]">
                      <xsl:message terminate="yes">A custom eviction policy is used for '/_default_' region. Starting with JBossCache 3.x the eviction API changed, so this config file will require manual transformation.</xsl:message>
                   </xsl:if>
                   <xsl:attribute name="algorithmClass">
                         <xsl:value-of
                               select="concat(substring-before(./config/region[@name='/_default_']/@policyClass,'Policy'), 'Algorithm')"/>
                      </xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                   <xsl:if
                         test="./config/attribute[@name='policyClass' and starts-with(string(.), 'org.jboss.cache.eviction')]">
                      <xsl:attribute name="algorithmClass">
                         <xsl:value-of
                               select="concat(substring-before(./config/attribute[@name='policyClass'],'Policy'), 'Algorithm')"/>
                      </xsl:attribute>
                   </xsl:if>
                </xsl:otherwise>
             </xsl:choose>
             <xsl:if
                   test="./config/attribute[@name='policyClass' and not(starts-with(string(.), 'org.jboss.cache.eviction'))]">
                <xsl:message terminate="yes">Custom eviction policies require manual transformation.</xsl:message>
             </xsl:if>
             <xsl:if test="./config/attribute[@name='eventQueueSize']">
                <xsl:attribute name="eventQueueSize">
                   <xsl:value-of select="normalize-space(./config/attribute[@name='eventQueueSize'])"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:for-each select="./config/region[@name='/_default_']/attribute">
                <xsl:call-template name="attributesSecondsSubstitution">
                   <xsl:with-param name="attr" select="."/>
                </xsl:call-template>
             </xsl:for-each>
          </default>
       </xsl:if>
       <xsl:for-each select="./config/region[@name!='/_default_']">
          <region>
             <xsl:if test="@name">
                <xsl:attribute name="name">
                   <xsl:value-of select="@name"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="@policyClass and starts-with(string(@policyClass), 'org.jboss.cache.eviction')">
                <xsl:attribute name="algorithmClass">
                   <xsl:value-of select="concat(substring-before(@policyClass,'Policy'), 'Algorithm')"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="@policyClass and not(starts-with(string(@policyClass), 'org.jboss.cache.eviction'))">
                <xsl:message terminate="yes">Custom eviction policies require manual transformation.</xsl:message>
             </xsl:if>
             <xsl:if test="not(@policyClass)">
                <xsl:attribute name="algorithmClass">
                   <xsl:value-of
                         select="concat(substring-before(../attribute[@name='policyClass'],'Policy'), 'Algorithm')"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="@eventQueueSize">
                <xsl:attribute name="eventQueueSize">
                   <xsl:value-of select="normalize-space(@eventQueueSize)"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="not(@eventQueueSize)">
                <xsl:attribute name="eventQueueSize">
                   <xsl:value-of select="normalize-space(../attribute[@name='eventQueueSize'])"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:for-each select="./*">
                <xsl:call-template name="attributesSecondsSubstitution">
                   <xsl:with-param name="attr" select="."/>
                </xsl:call-template>
             </xsl:for-each>
          </region>
       </xsl:for-each>
    </eviction>
 </xsl:template>

 <xsl:template name="attributesSecondsSubstitution">
    <xsl:param name="attr"/>
    <property>
       <xsl:choose>
          <xsl:when test="contains($attr/@name,'Seconds')">
             <xsl:attribute name="name">
                <xsl:value-of select="substring-before($attr/@name,'Seconds')"/>
             </xsl:attribute>
             <xsl:attribute name="value">
               <xsl:choose>
                  <xsl:when test="$attr &lt; 1">
                     <xsl:value-of select="-1"/>
                  </xsl:when>
                  <xsl:when test="$attr &gt; 0">
                     <xsl:value-of select="concat($attr,'000')"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="$attr"/>
                  </xsl:otherwise>
               </xsl:choose>
             </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
             <xsl:attribute name="name">
                <xsl:value-of select="string($attr/@name)"/>
             </xsl:attribute>
             <xsl:attribute name="value">
                <xsl:choose>
                   <xsl:when test="$attr/@name = 'maxNodes' and $attr = '0'">
                      <xsl:value-of select="-1"/>
                   </xsl:when>
                   <xsl:when test="$attr/@name = 'minNodes' and $attr = '0'">
                      <xsl:value-of select="-1"/>
                   </xsl:when>
                   <xsl:when test="$attr &lt; 0">
                      <xsl:value-of select="-1"/>
                   </xsl:when>
                   <xsl:otherwise>
                      <xsl:value-of select="$attr"/>
                   </xsl:otherwise>
                </xsl:choose>
             </xsl:attribute>
          </xsl:otherwise>
       </xsl:choose>
    </property>
 </xsl:template>

 <xsl:template match="//attribute[@name='CacheLoaderConfig'] | //attribute[@name='CacheLoaderConfiguration']">
    <loaders>
       <xsl:if test="./config/passivation">
          <xsl:attribute name="passivation">
             <xsl:value-of select="./config/passivation"/>
          </xsl:attribute>
       </xsl:if>
       <xsl:if test="./config/shared">
          <xsl:attribute name="shared">
             <xsl:value-of select="./config/shared"/>
          </xsl:attribute>
       </xsl:if>
       <xsl:if test="./config/preload">
          <preload>
             <xsl:call-template name="preloadTokenizer">
                <xsl:with-param name="string" select="./config/preload"/>
                <xsl:with-param name="delimiter" select="','"/>
             </xsl:call-template>
          </preload>
       </xsl:if>
       <xsl:for-each select="./config/cacheloader">
          <loader>
             <xsl:if test="./class">
                <xsl:attribute name="class">
                   <xsl:value-of select="./class"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="./async">
                <xsl:attribute name="async">
                   <xsl:value-of select="./async"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="./fetchPersistentState">
                <xsl:attribute name="fetchPersistentState">
                   <xsl:value-of select="./fetchPersistentState"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="./ignoreModifications">
                <xsl:attribute name="ignoreModifications">
                   <xsl:value-of select="./ignoreModifications"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="./purgeOnStartup">
                <xsl:attribute name="purgeOnStartup">
                   <xsl:value-of select="./purgeOnStartup"/>
                </xsl:attribute>
             </xsl:if>
             <xsl:if test="./properties">
                <xsl:copy-of select="./properties"/>
             </xsl:if>
             <xsl:if test="./singletonStore">
                <singletonStore>
                   <xsl:if test="./singletonStore/enabled">
                      <xsl:attribute name="enabled">
                         <xsl:value-of select="./singletonStore/enabled"/>
                      </xsl:attribute>
                   </xsl:if>
                   <xsl:if test="./singletonStore/enabled">
                      <xsl:attribute name="enabled">
                         <xsl:value-of select="./singletonStore/enabled"/>
                      </xsl:attribute>
                   </xsl:if>
                   <xsl:if test="./singletonStore/class">
                      <xsl:attribute name="class">
                         <xsl:value-of select="./singletonStore/class"/>
                      </xsl:attribute>
                   </xsl:if>
                   <xsl:copy-of select="./singletonStore/properties"/>
                </singletonStore>
             </xsl:if>
          </loader>
       </xsl:for-each>
    </loaders>
 </xsl:template>


 <xsl:template name="preloadTokenizer">
    <xsl:param name="string"/>
    <xsl:param name="delimiter" select="' '"/>
    <xsl:choose>
       <xsl:when test="$delimiter and contains($string, $delimiter)">
          <node>
             <xsl:attribute name="fqn">
                <xsl:value-of select="substring-before($string,$delimiter)"/>
             </xsl:attribute>
          </node>
          <xsl:call-template name="preloadTokenizer">
             <xsl:with-param name="string" select="substring-after($string,$delimiter)"/>
             <xsl:with-param name="delimiter" select="$delimiter"/>
          </xsl:call-template>
       </xsl:when>
       <xsl:otherwise>
          <node>
             <xsl:attribute name="fqn">
                <xsl:value-of select="$string"/>
             </xsl:attribute>
          </node>
       </xsl:otherwise>
    </xsl:choose>
 </xsl:template>

 <xsl:template name="transport">
    <xsl:if
          test="//attribute[@name='MultiplexerStack'] | //attribute[@name='ClusterConfig']">
       <jgroupsConfig>
          <xsl:if test="//attribute[@name='MultiplexerStack']">
             <xsl:attribute name="multiplexerStack">
                <xsl:value-of select="normalize-space(//attribute[@name='MultiplexerStack'])"/>
             </xsl:attribute>
          </xsl:if>
          <xsl:if test="//attribute[@name='ClusterConfig']">
                <xsl:copy-of select="//attribute[@name='ClusterConfig']/config/*"/>
          </xsl:if>
       </jgroupsConfig>
    </xsl:if>
 </xsl:template>

</xsl:stylesheet>
