# %%
import pandas as pd
import matplotlib.pyplot as plt
import os

multiple_files = True
custom_header = ['time', '0-1', '1-2', '2-3', '3-4', '4-5', '5-6', '6-10', '>10']

if(multiple_files): # folder with multiple csv files
    csv_folder = "velocity-reconstructions/"
    csv_folder = os.path.join(os.path.dirname(__file__), csv_folder)
    df = None
    for filename in os.listdir(csv_folder):
        if(df is None):
            df = pd.read_csv(csv_folder+filename, delimiter=' ', comment='#', names=custom_header)
        else:
            new_df = pd.read_csv(csv_folder+filename, delimiter=' ', comment='#',  names=custom_header)
            df = pd.concat([df, new_df], ignore_index=True)

else: # single csv file 
    csv_file_path = "velocity_simulation.csv"

    df = pd.read_csv(csv_file_path, delimiter=' ', comment='#', names=custom_header)

df = df.drop('time', axis=1)

columns_sums = df.sum(numeric_only=True)

total_sum = columns_sums.sum()
average_frequencies = columns_sums.div(total_sum)

average_frequencies.plot(kind='bar', color='skyblue')

extention = "png"


plt.xlabel('Velocities in km/h')
plt.ylabel("% Frequency")
plt.xticks(rotation=0)
if(multiple_files):
    plt.title('Distribution of individuals\' velocities (KABR data estimation)')
    plt.savefig('velocity_distribution_reconstruciton.' + extention, format=extention)
else:
    plt.title('Distribution of individuals\' velocities (Model simulation)')
    plt.savefig('velocity_distribution_simulation.' + extention, format=extention)

# %%
