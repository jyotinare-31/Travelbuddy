import psycopg2
from dotenv import load_dotenv
import os

load_dotenv()

try:
    connection = psycopg2.connect(
        host=os.getenv("DB_HOST"),
        port=os.getenv("DB_PORT"),
        database=os.getenv("DB_NAME"),
        user=os.getenv("DB_USER"),
        password=os.getenv("DB_PASSWORD")
    )

    print("PostgreSQL connection successful!")

    connection.close()

except Exception as e:
    print("Connection failed:")
    print(e)