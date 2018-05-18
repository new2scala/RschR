package org.ditw.rschr

import com.twitter.twittertext.Extractor

object TwitterTest extends App {
  val extractor = new Extractor()

  val text = "Emily Afton @ Sofar Sounds San Francisco singing about the Trump era and covering Frank Ocean. So moving. (@ Haight-Ashbury in San Francisco, CA) https://t.co/ydea3VN00v https://t.co/S4MT79CFo7"
  val text1 = "#deutsch Nieren-OP bei Melania #Trump https://t.co/ZIARQH48Z3"
  val hashtags = extractor.extractHashtags(text)
  val usernames = extractor.extractMentionedScreennames(text1)
  val entities = extractor.extractEntitiesWithIndices(text1)

  println(s"$hashtags/$usernames/$entities")

}