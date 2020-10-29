import sqlite3


class MessageSplitter():
    def __init__(self, data=":memory:"):
        self.conn = sqlite3.connect(data)
        self.c = self.conn.cursor()

    # CREATE TABLE messages (Id integer, ParentId integer, AcceptedAnswerId integer, CreationDate integer, Score integer, Body text, LastEditDate integer, LastActivityDate integer, CommentCount integer)

    def get_questions(self):
        selector = self.c.execute(
            "SELECT * from messages WHERE ParentId = 0 AND Id != 0 LIMIT 10")

        questions = [{
            "Id": m[0], 
            "AcceptedAnswerId": m[2], 
            "CreationDate": m[3], 
            "Body": m[5],
            "CommentCount": m[8]
            } for m in selector]
        return questions

    def get_answers(self, parentId: int):
        selector = self.c.execute(
            f"SELECT * from messages WHERE ParentId = {parentId}")
        
        answers = [{
            "Id": m[0], 
            "ParentId": m[1], 
            "CreationDate": m[3], 
            "Body": m[5],
            "CommentCount": m[8]
            } for m in selector]
        return answers

if __name__ == "__main__":
    p = MessageSplitter(data=R"./dump/messages1perc.db")
    # print(p.get_questions())
    print(p.get_answers(63259787))
