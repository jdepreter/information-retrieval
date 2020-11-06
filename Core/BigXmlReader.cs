using System;
using System.IO;
using System.Xml;
using System.Xml.Serialization;

namespace BigXMLReader
{
    [XmlRoot("row")]
    public class Message
    {
        [XmlAttribute]
        public int Id { get; set; }
        [XmlAttribute]
        public int PostTypeId { get; set; }
        [XmlAttribute]
        public int ParentId { get; set; }
        [XmlAttribute]
        public int AcceptedAnswerId { get; set; }
        [XmlAttribute]
        public string CreationDate { get; set; }
        [XmlAttribute]
        public int Score { get; set; }
        [XmlAttribute]
        public string Title { get; set; }
        [XmlAttribute]
        public string Body { get; set; }
        [XmlAttribute]
        public string Tags { get; set; }
        [XmlAttribute]
        public int CommentCount { get; set; }

        public override string ToString()
        {
            return $"ID: {Id} | Date: {CreationDate} | {((ParentId == 0) ? 'Q' : 'R')}";
        }

        public string MakeXmlFriendly(string text)
        {
            return text.Replace("&", "&amp;").Replace("<", "&lt;").Replace(">", "&gt;");
        }

        public string ToXml()
        {
            if (PostTypeId == 1)
            {
                return $"<Id>{Id}</Id>\n<AcceptedAnswerId>{AcceptedAnswerId}</AcceptedAnswerId>\n<CreationDate>{CreationDate}</CreationDate>\n<Title>{MakeXmlFriendly(Title)}</Title>\n<Body>{MakeXmlFriendly(Body)}</Body>\n<Tags>{MakeXmlFriendly(Tags)}</Tags>\n<CommentCount>{CommentCount}</CommentCount>";
            } else if (PostTypeId == 2)
            {
                return $"<Id>{Id}</Id>\n<CreationDate>{CreationDate}</CreationDate>\n<Body>{MakeXmlFriendly(Body)}</Body>\n<CommentCount>{CommentCount}</CommentCount>";
            }
            return "";
        }
    }

    class Program
    {
        static void Main(string[] args)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(Message));
            XmlReaderSettings settings = new XmlReaderSettings();
            settings.IgnoreWhitespace = true;
            settings.ConformanceLevel = ConformanceLevel.Fragment;
            using Stream stream = File.OpenRead(args[1]);
            try
            {
                stream.Seek((long)(stream.Length * Convert.ToDouble(args[0])), SeekOrigin.Begin);
                while (stream.ReadByte() != (byte)'\n') { }
                using (XmlReader reader = XmlReader.Create(stream, settings))
                {
                    // Read xml file and parse lines
                    int startID = -1;
                    bool reading = reader.Read();
                    while (reading)
                    {
                        if (reader.NodeType == XmlNodeType.Element && reader.Name == "row")
                        {
                            try
                            {
                                Message m = (Message)serializer.Deserialize(reader.ReadSubtree());
                                // Keeps track of first message ID
                                if (startID == -1)
                                {
                                    startID = m.Id;
                                }
                                // If message is question, create file and add contents
                                if (m.PostTypeId == 1)
                                {
                                    string path = Path.Combine(args[2], $"{m.Id}.xml");
                                    File.WriteAllLines(path, new[] { $"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<root>\n{m.ToXml()}\n<Answers>" });
                                } 
                                // If message is answer AND it's on a question in our subset, add answer to question file
                                else if (m.PostTypeId == 2 && m.ParentId >= startID)
                                {
                                    string path = Path.Combine(args[2], $"{m.ParentId}.xml");
                                    File.AppendAllLines(path, new[] { $"<item>\n{m.ToXml()}\n</item>" });
                                }
                                reading = reader.Read();
                            } catch (XmlException e) { Console.WriteLine($"Failed parsing: {e.Message}"); }
                        }
                    }

                    string[] files = Directory.GetFiles(args[2]);

                    // Add xml closing root tag to files
                    foreach (string fileName in files)
                    {
                        string path = Path.Combine(args[2], fileName);
                        File.AppendAllLines(path, new[] { $"</Answers>\n</root>" });
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
            Console.WriteLine("Done!");
        }
    }
}
