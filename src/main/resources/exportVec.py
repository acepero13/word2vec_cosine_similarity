#!/usr/bin/python
import io
import sqlite3


def load_vectors(fname):
    conn = sqlite3.connect('embeddings.db')
    c = conn.cursor()
    # Create table
    c.execute('''CREATE TABLE embeddings
             (word text PRIMARY KEY, embeddings text)''')

    fin = io.open(fname, 'r', encoding='utf-8', newline='\n', errors='ignore')
    n, d = map(int, fin.readline().split())
    data = {}
    insertIntoDb(c, conn, fin)
    conn.close()
    return data


def insertIntoDb(c, conn, fin):
    counter = 0
    for line in fin:
        tokens = line.rstrip().split(' ')
        vectors = " ".join(tokens[1:])
        if (tokens[0]) == "'":
            continue
        c.execute("INSERT INTO embeddings VALUES ('" +
                  tokens[0] + "','" + vectors + "' )")
        if counter % 100 == 0:
            conn.commit()
        counter += 1


if __name__ == "__main__":
    vecs = load_vectors('cc.de.300.vec')
    print("Finished loading")
