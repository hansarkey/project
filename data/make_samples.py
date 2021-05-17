#simple script to make vectors out of our data

import csv
import re
import pandas as pd
from collections import defaultdict
from dateutil import parser
import numpy as np
from scipy import spatial


if __name__ == '__main__':

    # grab category from files. Use set to get only unique values
    cityList = set()
    cuisineList = set()
    with open('zomato_vectors3.txt', 'r') as f:
        with open('city_samples.txt', 'w') as x:
            with open('cuisine_samples.txt', 'w') as y:
                for line in f.readlines():
                    city = line.split(",")[7].lower()
                    cityGlove = line.split(",")[1]
                    if city not in cityList:
                        cityList.add(city)
                        x.write("{}:{}\n".format(cityGlove,city))
                        x.write("{}:I am in {}\n".format(cityGlove,city))
                        x.write("{}:I live in {}\n".format(cityGlove,city))
                        x.write("{}:I am currently living in {}\n".format(cityGlove,city))
                    cuisine = line.split(",")[8].strip().lower()
                    if cuisine not in cuisineList:
                        print(cuisine)
                        cuisineList.add(cuisine)
                        cuisineGlove = line.split(",")[2]
                        y.write("{}:{}\n".format(cuisineGlove,cuisine))
                        y.write("{}:I would like {}\n".format(cuisineGlove,cuisine))
                        y.write("{}:I want to eat {}\n".format(cuisineGlove, cuisine))
                        y.write("{}:Maybe some {}\n".format(cuisineGlove, cuisine))

