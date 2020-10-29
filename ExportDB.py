import sqlite3


"""
int Id 
int PostTypeId 
int ParentId 
int AcceptedAnswerId 
string CreationDate 
int Score 
string Title 
string Body 
string Tags 
int CommentCount 
"""


class MessageSplitter():
    def __init__(self, data=":memory:"):
        self.conn = sqlite3.connect(data)
        self.c = self.conn.cursor()

    # CREATE TABLE messages (Id integer, ParentId integer, AcceptedAnswerId integer, CreationDate integer, Score integer, Body text, LastEditDate integer, LastActivityDate integer, CommentCount integer)

    def get_questions(self):
        selector = self.c.execute(
            "SELECT * from messages WHERE PostTypeId = 1 LIMIT 10")

        questions = [{
            "Id": m[0], 
            "AcceptedAnswerId": m[3], 
            "CreationDate": m[4], 
            "Title": m[6],
            "Body": m[7],
            "Tags": m[8],
            "CommentCount": m[9]
            } for m in selector]
        return questions

    def get_answers(self, parentId: int):
        selector = self.c.execute(
            f"SELECT * from messages WHERE PostTypeId = 2 AND ParentId = {parentId}")
        
        answers = [{
            "Id": m[0], 
            "ParentId": m[2], 
            "CreationDate": m[4], 
            "Body": m[7],
            "CommentCount": m[9]
            } for m in selector]
        return answers

    def test(self):
        selector = self.c.execute(
            "SELECT * from messages WHERE PostTypeId = 5 LIMIT 5")
        return [i for i in selector]

if __name__ == "__main__":
    p = MessageSplitter(data=R"./dump/messages.db")
    print(p.test())
    # print(p.get_answers(63259787))
