<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateToDosTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('to_dos', function (Blueprint $table) {
            $table->bigIncrements('todoID');
            $table->string('status');
            $table->string('description');

            $table->string('userID');
            $table->foreign('userID')->references('userID')->on('users')->onDelete('cascade');

            $table->unsignedBigInteger('boardID');
            $table->foreign('boardID')->references('boardID')->on('boards')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('to_dos');
    }
}
